package com.souiid.demoproject.platform

import android.content.Context

object AppContextProvider {
    lateinit var context: Context
        private set

    fun initialize(context: Context) {
        this.context = context.applicationContext
    }
}