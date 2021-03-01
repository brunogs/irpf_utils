package br.com.bgs.irpfutils.domain

data class Stock (
    val code: String,
    val companyName: String,
    val type: StockType
)