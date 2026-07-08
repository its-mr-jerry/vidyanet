package com.kastack.vidyanet.services.auth

import com.kastack.vidyanet.plugins.NotFoundException
import com.kastack.vidyanet.plugins.UnauthorizedException
import com.kastack.vidyanet.plugins.generateToken
import com.kastack.vidyanet.database.entities.UserEntity
import com.kastack.vidyanet.database.entities.toDto
import com.kastack.vidyanet.database.tables.OtpsTable
import com.kastack.vidyanet.database.tables.UsersTable
import com.kastack.vidyanet.database.toKotlinx
import com.kastack.vidyanet.models.auth.LoginRequest
import com.kastack.vidyanet.models.auth.LoginResponse
import com.kastack.vidyanet.models.user.UserStatus
import com.kastack.vidyanet.models.user.UserType
import com.kastack.vidyanet.models.auth.VerifyOtpRequest
import kotlinx.datetime.toStdlibInstant
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

class AuthServices {

    suspend fun sendOtp(request: LoginRequest) {
        val userExists = transaction {
            !UserEntity.find { UsersTable.phone eq request.phone }.empty()
        }

        if (!userExists) {
            throw NotFoundException("User not found with this phone number.")
        }

//        val otpCode = (100000..999999).random().toString()
        val otpCode = "123456"
        val expiry = Clock.System.now() + 5.minutes

        transaction {
            OtpsTable.upsert {
                it[phone] = request.phone
                it[otp] = otpCode
                it[expiresAt] = expiry.toKotlinx()
            }
        }
    }

    private fun createUser(request: LoginRequest) {
        val status = when (request.userType) {
            UserType.PLATFORM_OWNER -> UserStatus.ACTIVE
            else -> UserStatus.PENDING_VERIFICATION
        }

        transaction {
            UserEntity.new {
                this.phone = request.phone
                this.userType = request.userType
                this.status = status
            }
        }
    }

    suspend fun verifyOtp(request: VerifyOtpRequest): LoginResponse {
        val now = Clock.System.now()

        return transaction {
            val otpData = OtpsTable.selectAll().where { OtpsTable.phone eq request.phone }.firstOrNull()
                ?: throw UnauthorizedException("No OTP found for this phone number. Please request a new one.")

            if (otpData[OtpsTable.otp] != request.otp) {
                throw UnauthorizedException("Invalid OTP code.")
            }

            if (otpData[OtpsTable.expiresAt].toStdlibInstant() < now.toKotlinx().toStdlibInstant()) {
                OtpsTable.deleteWhere { phone eq request.phone }
                throw UnauthorizedException("OTP has expired. Please request a new one.")
            }

            // OTP is valid, delete it so it can't be used again
            OtpsTable.deleteWhere { phone eq request.phone }

            val user = UserEntity.find { UsersTable.phone eq request.phone }.firstOrNull()?.apply {
                lastLoginAt = Clock.System.now().toKotlinx()
                isPhoneVerified = true
                if (status == UserStatus.PENDING_VERIFICATION) {
                    status = UserStatus.ACTIVE
                }
            } ?: throw UnauthorizedException("User no longer exists.")

            val roles = user.roles.map { it.roleCode }

            val token = generateToken(
                id = user.id.value,
                phone = user.phone,
                userType = user.userType.name,
                roles = roles
            )

            LoginResponse(
                token = token,
                user = user.toDto()
            )
        }
    }


}
