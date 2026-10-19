package com.souiid.mutiplatformtemplate

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform