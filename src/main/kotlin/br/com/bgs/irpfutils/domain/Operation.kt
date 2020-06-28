package br.com.bgs.irpfutils.domain

import java.math.BigDecimal

data class Operation(
    var operationType: OperationType? = null,
    var market: String? = null,
    var title: String? = null,
    var quantity: Int? = null,
    var unitPrice: BigDecimal? = null,
    var operationPrice: BigDecimal? = null,
    var operationDate: String? = null,
    var averagePrice: BigDecimal? = null
)