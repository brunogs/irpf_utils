package br.com.bgs.irpfutils.domain

//ON = ordinárias (3)
//PN = preferenciais (4)
//PNA = classes A (5)
//PNB = classe B (6)
//PNC = classe C (7)
//PND = classe D (8)
//UNT = Units(11)
enum class StockType(val description: String, val codeValue: Int, val code: String) {
    ON("ordinárias (3)", 3, "ON"),
    PN("preferenciais (4)", 4, "PN"),
    PNA("classes A (5)", 5, "PNA"),
    PNB("classe B (6)", 6, "PNB"),
    PNC("classe C (7)", 7, "PNC"),
    PND("classe D (8)", 8, "PND"),
    UNT("Units(11)", 11, "UNT")
}