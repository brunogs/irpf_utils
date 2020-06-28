package br.com.bgs.irpfutils.domain

import java.math.BigDecimal

//TODO add another generics taxes
data class Negotiation(
    val operations: List<Operation>,
    val netValueOperations: BigDecimal,
    val settlementTax: BigDecimal,
    val registerTax: BigDecimal,
    val negotiationDate: String
)