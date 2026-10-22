package com.souiid.demoproject.domain.repository

interface ISignUpRepository {
    suspend fun signUp(email: String, password: String)
}