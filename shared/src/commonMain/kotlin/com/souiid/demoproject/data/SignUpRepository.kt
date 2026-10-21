package com.souiid.demoproject.data

import com.souiid.demoproject.domain.model.SignUpInfo
import com.souiid.demoproject.domain.model.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignUpRepository {

    val auth = Firebase.auth
    val firestore = Firebase.firestore

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

    suspend fun signUp(email: String, password: String) {
        val uid = requireNotNull(auth.createUserWithEmailAndPassword(email, password).user).uid

        val info = signUpInfo.value

        val user = User(
            id = uid,
            firstName = info.firstName,
            lastName = info.lastName,
            description = info.description
        )

        firestore.collection("users").document(uid).set(user)
        clear()
    }

    fun clear() {
        _signUpInfo.value = SignUpInfo()
    }
}