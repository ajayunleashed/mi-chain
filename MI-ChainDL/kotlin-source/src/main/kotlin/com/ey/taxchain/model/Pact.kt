package com.ey.taxchain.model

import net.corda.core.serialization.CordaSerializable

@CordaSerializable
data class Pact(val referenceNumber: String, val totalAmount: Double)