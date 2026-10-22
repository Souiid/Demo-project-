package com.souiid.demoproject.domain.repository

interface IAuthRepository {
    suspend fun signIn(email: String, password: String)
}