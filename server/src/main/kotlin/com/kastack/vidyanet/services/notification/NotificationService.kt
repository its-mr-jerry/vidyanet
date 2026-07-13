package com.kastack.vidyanet.services.notification

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import com.kastack.vidyanet.config.AppConfig
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream

object NotificationService {
    private val logger = LoggerFactory.getLogger(NotificationService::class.java)
    private var initialized = false

    fun init() {
        if (initialized) return
        
        try {
            val serviceAccountJson = AppConfig.firebaseServiceAccountJson
            if (serviceAccountJson.isEmpty()) {
                logger.warn("FIREBASE_SERVICE_ACCOUNT_JSON not found in environment. Push notifications will not work.")
                return
            }

            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(ByteArrayInputStream(serviceAccountJson.toByteArray())))
                .build()

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options)
            }
            initialized = true
            logger.info("Firebase Admin SDK initialized successfully.")
        } catch (e: Exception) {
            logger.error("Failed to initialize Firebase Admin SDK", e)
        }
    }

    fun sendPushNotification(token: String?, title: String, body: String, data: Map<String, String> = emptyMap()) {
        if (!initialized || token.isNullOrBlank()) {
            logger.warn("Push notification not sent: FCM token is null or Firebase not initialized")
            return
        }

        try {
            val message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build())
                .putAllData(data)
                .build()

            val response = FirebaseMessaging.getInstance().send(message)
            logger.info("Successfully sent message: $response")
        } catch (e: Exception) {
            logger.error("Error sending FCM message", e)
        }
    }

    fun sendMulticastPushNotification(tokens: List<String>, title: String, body: String, data: Map<String, String> = emptyMap()) {
        if (!initialized || tokens.isEmpty()) {
            logger.warn("Multicast notification not sent: No tokens or Firebase not initialized")
            return
        }

        try {
            val message = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build())
                .putAllData(data)
                .build()

            val response = FirebaseMessaging.getInstance().sendEachForMulticast(message)
            logger.info("Successfully sent multicast message: ${response.successCount} success, ${response.failureCount} failure")
        } catch (e: Exception) {
            logger.error("Error sending multicast FCM message", e)
        }
    }
}
