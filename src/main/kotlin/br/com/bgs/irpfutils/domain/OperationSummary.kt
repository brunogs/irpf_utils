package br.com.bgs.irpfutils.domain

import java.math.BigDecimal

data class OperationSummary(
    val company: String,
    val averagePrice: BigDecimal,
    val cost: BigDecimal,
    val quantity: BigDecimal,
    val firstPrice: BigDecimal,
    val lastPrice: BigDecimal
)