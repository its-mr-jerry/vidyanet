package com.kastack.vidyanet.config
import io.github.cdimascio.dotenv.dotenv

object AppConfig {
    private val dotenv = try {
        dotenv {
            // Try current directory first, then parent directory (root)
            directory = if (java.io.File("./.env").exists()) "./" else "../"
            ignoreIfMissing = true
        }
    } catch (e: Exception) {
        println("Dotenv initialization failed: ${e.message}")
        null
    }

    fun get(key: String, defaultValue: String = ""): String {
        return System.getenv(key) ?: dotenv?.get(key) ?: defaultValue
    }

    val r2AccountId = get("R2_ACCOUNT_ID")
    val r2AccessKeyId = get("R2_ACCESS_KEY_ID")
    val r2SecretAccessKey = get("R2_SECRET_ACCESS_KEY")
    val r2BucketName = get("R2_BUCKET_NAME")
    val r2PublicUrl = get("R2_PUBLIC_URL")

    // Database
    val dbName = get("DB_NAME", "vidya_net_db")
    val dbUser = get("DB_USER", "vidya_net")
    val dbPassword = get("DB_PASSWORD", "7209")
    val dbHost = get("DB_HOST", "localhost")
    val dbPort = get("DB_PORT", "5432").toIntOrNull() ?: 5432
    val authSecret = get("AUTH_SECRET","in=house-erp-secret-key")
    val jwtAudience = get("JWT_AUDIENCE", "jwt-audience")
    val jwtDomain = get("JWT_DOMAIN", "https://jwt-provider-domain/")
    val jwtRealm = get("JWT_REALM", "in-house-erp-admin")
}
