package com.kastack.vidyanet.database

import kotlin.time.Instant as KotlinInstant
import kotlinx.datetime.Instant as KotlinxInstant

@Suppress("DEPRECATION")
fun KotlinInstant.toKotlinx(): KotlinxInstant =
    KotlinxInstant.fromEpochMilliseconds(this.toEpochMilliseconds())

@Suppress("DEPRECATION")
fun KotlinxInstant.toKotlin(): KotlinInstant =
    KotlinInstant.fromEpochMilliseconds(this.toEpochMilliseconds())