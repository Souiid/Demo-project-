package com.souiid.demoproject.platform

import dev.gitlive.firebase.storage.File
import platform.Foundation.NSURL

actual fun createFileFromUri(uri: String): File {
    val nsUrl = if (uri.startsWith("file://")) {
        NSURL.URLWithString(uri)
    } else {
        NSURL.fileURLWithPath(uri)
    }
    return File(nsUrl!!)
}