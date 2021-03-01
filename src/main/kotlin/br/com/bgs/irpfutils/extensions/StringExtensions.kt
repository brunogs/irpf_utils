package br.com.bgs.irpfutils.extensions

fun String?.cleanupAcronyms() = this?.replace(" (ON|CI|S|ERS|ER|ED|EJ|NM|N\\d)".toRegex(), "")
    ?.replace(" #(\\d+)?".toRegex(), "")
    ?.replace("  +".toRegex(), " ")
    ?.trim()