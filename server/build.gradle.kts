plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
}

group = "com.kastack.vidyanet"
version = "1.0.0"
application {
    mainClass = "com.kastack.vidyanet.ApplicationKt"
}

dependencies {
    api(projects.core)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.serverCors)
    implementation(libs.ktor.serverCachingHeaders)
    implementation(libs.ktor.serverAuth)
    implementation(libs.ktor.serverAuthJwt)
    implementation(libs.ktor.serverResources)
    implementation(libs.ktor.serverStatusPages)
    implementation(libs.ktor.serverContentNegotiation)
    implementation(libs.ktor.serializationKotlinxJson)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.exposed.migration)
    implementation(libs.flyway.core)
    implementation(libs.flyway.postgresql)
    implementation(libs.kotlinx.datetime)
    implementation(libs.hikaricp)
    implementation(libs.postgresql)
    implementation(libs.aws.s3)
    implementation(libs.dotenv.kotlin)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.ktor.client.core)
    testImplementation(libs.ktor.client.contentNegotiation)
    testImplementation(libs.kotlin.testJunit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.h2)
}
