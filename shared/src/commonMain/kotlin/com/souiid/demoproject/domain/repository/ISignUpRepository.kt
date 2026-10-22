package com.souiid.demoproject.domain.repository

import com.souiid.demoproject.domain.model.SignUpInfo
import kotlinx.coroutines.flow.StateFlow

interface ISignUpRepository {

    val signUpInfo: StateFlow<SignUpInfo>

    suspend fun signUp(email: String, password: String)
    fun updateFirstName(value: String)
    fun updateLastName(value: String)
    fun updateDescription(value: String)
    fun clear()
}