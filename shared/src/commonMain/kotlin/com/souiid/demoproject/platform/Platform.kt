package com.souiid.demoproject.platform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform