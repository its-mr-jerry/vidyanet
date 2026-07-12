package com.kastack.vidyanet.utils

import kotlin.time.Instant as KotlinInstant
import kotlinx.datetime.Instant as KotlinxInstant

/**
 * Converts modern kotlin.time.Instant to kotlinx.datetime.Instant
 */
fun KotlinInstant.toKotlinx(): KotlinxInstant =
    KotlinxInstant.fromEpochMilliseconds(this.toEpochMilliseconds())

/**
 * Converts kotlinx.datetime.Instant to modern kotlin.time.Instant
 */
fun KotlinxInstant.toKotlin(): KotlinInstant =
    KotlinInstant.fromEpochMilliseconds(this.toEpochMilliseconds())
