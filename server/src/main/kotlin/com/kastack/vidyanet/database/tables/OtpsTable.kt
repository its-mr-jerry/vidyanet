package com.kastack.vidyanet.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object OtpsTable : Table("otps") {
    val phone = varchar("phone", 15)
    val otp = varchar("otp", 6)
    val expiresAt = timestamp("expires_at")
    
    override val primaryKey = PrimaryKey(phone)
}
