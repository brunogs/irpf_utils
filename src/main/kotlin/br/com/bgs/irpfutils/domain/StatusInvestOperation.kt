package br.com.bgs.irpfutils.domain

import java.math.BigDecimal

data class StatusInvestOperation(
    var dataOperacao: String? = "",
    var categoria: String? = "",
    var codigoAtivo: String? = "",
    var operacao: String? = "",
    var quantidade: Int? = 0,
    var precoUnitario: BigDecimal? = BigDecimal.ZERO,
    var corretora: String? = "CLEAR CORRETORA",
    var corretagem: BigDecimal? = BigDecimal.ZERO,
    var taxas: BigDecimal? = BigDecimal.ZERO,
    var impostos: BigDecimal? = BigDecimal.ZERO,
    var irrf: BigDecimal? = BigDecimal.ZERO
)