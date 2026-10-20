package com.souiid.demoproject.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SignUpRepository {
    private val _firstName = MutableStateFlow("")
    val firstName = _firstName.asStateFlow()

    private val _lastName = MutableStateFlow("")
    val lastName = _lastName.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    fun updateFirstName(value: String) {
        _firstName.value = value
    }

    fun updateLastName(value: String) {
        _lastName.value = value
    }

    fun updateDescription(value: String) {
        _description.value = value
    }

    fun clear() {
        _firstName.value = ""
        _lastName.value = ""
        _description.value = ""
    }
}