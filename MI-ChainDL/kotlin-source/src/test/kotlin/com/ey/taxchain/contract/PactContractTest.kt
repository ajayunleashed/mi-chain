package com.ey.taxchain.contract

import com.ey.taxchain.flow.CreatePactFlow
import com.ey.taxchain.model.Pact
import com.ey.taxchain.state.PactState
import net.corda.core.utilities.getOrThrow
import net.corda.testing.core.singleIdentity
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.StartedMockNode
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.Instant
import kotlin.test.assertTrue
import kotlin.test.fail

class PactContractTest {
    lateinit var net: MockNetwork
    lateinit var a: StartedMockNode
    lateinit var b: StartedMockNode
    @Before
    fun setup() {
        net = MockNetwork(listOf("com.ey.taxchain.contract"))
        a = net.createPartyNode()
        b = net.createPartyNode()
        // For real nodes this happens automatically, but we have to manually register the flow for tests
        listOf(a, b).forEach { it.registerInitiatedFlow(CreatePactFlow.Acceptor::class.java) }
        net.runNetwork()
    }
    @After
    fun tearDown() {
        net.stopNodes()
    }
    @Test
    fun `Order amount must greater than zero`(){
        val state = PactState(Pact("a01",0.0),
                a.info.singleIdentity(),
                b.info.singleIdentity(),
                Instant.now()
        )
        try {
            val flow = CreatePactFlow.Initiator(state, b.info.singleIdentity())
            val future = a.startFlow(flow)
            net.runNetwork()
            future.getOrThrow()
            fail("No exception thrown!!")
        } catch(e:Exception){
            assertTrue(e.message.toString().contains(Regex("Contract verification failed: Failed requirement")))
        }
    }
}