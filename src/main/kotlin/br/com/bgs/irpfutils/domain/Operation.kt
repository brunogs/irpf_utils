package br.com.bgs.irpfutils.domain

import java.math.BigDecimal

data class Operation(
    val operationType: OperationType,
    val market: String,
    val title: String,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val operationPrice: BigDecimal,
    var operationDate: String? = null
)