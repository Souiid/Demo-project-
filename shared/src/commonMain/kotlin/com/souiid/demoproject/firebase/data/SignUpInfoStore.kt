package com.souiid.demoproject.firebase.data

import com.souiid.demoproject.firebase.domain.model.SignUpInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignUpInfoStore {

    private val _signUpInfo = MutableStateFlow(SignUpInfo())
    val signUpInfo = _signUpInfo.asStateFlow()

    fun updateFirstName(value: String) {
        _signUpInfo.update { it.copy(firstName = value) }
    }

    fun updateLastName(value: String) {
        _signUpInfo.update { it.copy(lastName = value) }
    }

    fun updateDescription(value: String) {
        _signUpInfo.update { it.copy(description = value) }
    }

    fun clear() {
        _signUpInfo.value = SignUpInfo()
    }
}