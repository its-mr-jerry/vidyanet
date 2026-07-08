package com.kastack.vidyanet.utils

import kotlin.math.roundToLong
import kotlin.math.pow

fun Double.format(digits: Int): String {
    val factor = 10.0.pow(digits)
    val rounded = (this * factor).roundToLong() / factor
    val s = rounded.toString()
    if (digits <= 0) return s.substringBefore(".")
    val parts = s.split(".")
    val integerPart = parts[0]
    val decimalPart = if (parts.size > 1) parts[1] else ""
    return integerPart + "." + decimalPart.padEnd(digits, '0').take(digits)
}

fun Double.toCurrency(): String = "₹${this.format(2)}"
