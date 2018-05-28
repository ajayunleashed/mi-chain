package com.ey.taxchain.plugin

import com.ey.taxchain.api.PactApi
import net.corda.core.messaging.CordaRPCOps
import net.corda.webserver.services.WebServerPluginRegistry
import java.util.function.Function

class PactPlugin : WebServerPluginRegistry {
    /**
     * A list of classes that expose web APIs.
     */
    override val webApis: List<Function<CordaRPCOps, out Any>> = listOf(Function(::PactApi))

    /**
     * A list of directories in the resources directory that will be served by Jetty under /web.
     */
    override val staticServeDirs: Map<String, String> = mapOf(
            // This will serve the taxChainWeb directory in resources to /web/taxchain
            "taxchain" to javaClass.classLoader.getResource("taxChainWeb").toExternalForm()
    )
}