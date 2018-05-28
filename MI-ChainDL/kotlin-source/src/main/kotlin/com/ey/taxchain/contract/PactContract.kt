package com.ey.taxchain.contract

import com.ey.taxchain.state.PactState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.contracts.requireThat
import net.corda.core.crypto.SecureHash
import net.corda.core.transactions.LedgerTransaction

open class PactContract : Contract {
    companion object {
        @JvmStatic
        val ORDER_CONTRACT_ID = "com.ey.taxchain.contract.PactContract"
    }
    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands.Create>()

        when (command.value) {
            is Commands.Create -> {
                // Issuance verification logic.
                requireThat {
                // Generic constraints around the order transaction.
                    "No inputs should be consumed when issuing an order." using (tx.inputs.isEmpty())
                    "Only one output state should be created." using (tx.outputs.size == 1)
                    val out = tx.outputsOfType<PactState>().single()
                    "The seller and the buyer cannot be the same entity." using (out.seller != out.buyer)
                    "The amount cannot be 0." using (out.order.totalAmount != 0.0)

                }
            }

        }
    }

    /**
     * This contract only implements one command, Create.
     */
    interface Commands : CommandData {
        class Create : Commands
    }
    /** This is a reference to the underlying legal contract template and associated parameters. */
    /*override*/ val legalContractReference: SecureHash = SecureHash.sha256("contract template and params")
}
