package com.souiid.demoproject

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform