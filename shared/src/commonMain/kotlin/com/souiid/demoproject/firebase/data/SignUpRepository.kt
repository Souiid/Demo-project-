package com.souiid.demoproject.firebase.data

import com.souiid.demoproject.firebase.domain.model.User
import com.souiid.demoproject.firebase.domain.repository.ISignUpRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

class SignUpRepository(
    private val store: SignUpInfoStore
) : ISignUpRepository {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    override val signUpInfo = store.signUpInfo

    override fun updateFirstName(value: String) = store.updateFirstName(value)
    override fun updateLastName(value: String) = store.updateLastName(value)
    override fun updateDescription(value: String) = store.updateDescription(value)
    override fun clear() = store.clear()

    override suspend fun signUp(email: String, password: String) {
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
}