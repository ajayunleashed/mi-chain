package com.ey.taxchain.api


import com.ey.taxchain.flow.CreatePactFlow
import com.ey.taxchain.model.Pact
import com.ey.taxchain.state.PactState
import net.corda.core.contracts.StateAndRef
import net.corda.core.identity.CordaX500Name
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.messaging.startTrackedFlow
import net.corda.core.messaging.vaultQueryBy
import net.corda.core.utilities.getOrThrow
import net.corda.core.utilities.loggerFor
import org.slf4j.Logger
import java.time.Instant
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

val NOTARY_NAME = "Controller"
val NETWORK_MAP_NAME = "Network Map Service"

// This API is accessible from /taxchain/. All paths specified below are relative to it.
@Path("taxchain")
class PactApi (val services: CordaRPCOps) {
    private val myLegalName: CordaX500Name = services.nodeInfo().legalIdentities.first().name

    companion object {
        private val logger: Logger = loggerFor<PactApi>()
    }

    /**
     * Returns the node's name.
     */
    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    fun whoami() = mapOf("me" to myLegalName)

    /**
     * Returns all parties registered with the [NetworkMapService]. These names can be used to look up identities
     * using the [IdentityService].
     */
    @GET
    @Path("peers")
    @Produces(MediaType.APPLICATION_JSON)
    fun getPeers(): Map<String, List<CordaX500Name>> {
        val nodeInfo = services.networkMapSnapshot()
        return mapOf("peers" to nodeInfo
                .map { it.legalIdentities.first().name }
                //filter out myself, notary and eventual network map started by driver
                .filter { it != myLegalName && it.organisation != NOTARY_NAME && it.organisation != NETWORK_MAP_NAME })
    }


    @GET
    @Path("pacts")
    @Produces(MediaType.APPLICATION_JSON)
    fun getOrders(): List<StateAndRef<PactState>> {
        val vaultStates = services.vaultQueryBy<PactState>()
        return vaultStates.states
    }


    @PUT
    @Path("{seller}/{buyer}/create-pact")
    fun createOrder(order: Pact,
                    @PathParam("seller") sellerName: CordaX500Name,
                    @PathParam("buyer") buyerName: CordaX500Name): Response {
        val seller = services.wellKnownPartyFromX500Name(sellerName) ?:
        return Response.status(Response.Status.BAD_REQUEST).entity("Party named $sellerName cannot be found.\n").build()

        val buyer = services.wellKnownPartyFromX500Name(buyerName) ?:
        return Response.status(Response.Status.BAD_REQUEST).entity("Party named $buyerName cannot be found.\n").build()
        val state = PactState(
                order,
                seller,
                buyer,
                date = Instant.now()
        )
        val (status, msg) = try {
            val flowHandle = services.startTrackedFlow(CreatePactFlow::Initiator, state, seller)
            flowHandle.progress.subscribe { println(">> $it") }
            val result = flowHandle.returnValue.getOrThrow()
            Response . Status . CREATED to "Transaction id ${result.id} committed to ledger."
        } catch (ex: Throwable) {
            logger.error(ex.message, ex)
            Response . Status . BAD_REQUEST to "Transaction failed."
        }
        return Response.status(status).entity(msg).build()
    }
}