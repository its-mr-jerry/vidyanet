package com.kastack.vidyanet

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform