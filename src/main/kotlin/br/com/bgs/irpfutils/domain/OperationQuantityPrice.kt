package br.com.bgs.irpfutils.domain

import java.math.BigDecimal

data class OperationQuantityPrice(
    var averagePrice: BigDecimal,
    var quantity: BigDecimal
)