package com.souiid.demoproject.platform

import android.net.Uri
import dev.gitlive.firebase.storage.File
import androidx.core.net.toUri

actual fun createFileFromUri(uri: String): File {
    val androidUri = if (uri.startsWith("content://") || uri.startsWith("file://")) {
        uri.toUri()
    } else {
        Uri.fromFile(java.io.File(uri))
    }
    return File(androidUri)
}