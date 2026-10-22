package com.souiid.demoproject.data

import com.souiid.demoproject.domain.model.SignUpInfo
import kotlin.test.Test
import kotlin.test.assertEquals

class SignUpInfoStoreTest {

    private val store = SignUpInfoStore()

    @Test
    fun initialStateIsEmpty() {
        assertEquals(SignUpInfo(), store.signUpInfo.value)
    }

    @Test
    fun firstNameIsUpdated() {
        store.updateFirstName("John")
        assertEquals("John", store.signUpInfo.value.firstName)
    }

    @Test
    fun lastNameIsUpdated() {
        store.updateLastName("Doe")
        assertEquals("Doe", store.signUpInfo.value.lastName)
    }

    @Test
    fun descriptionIsUpdated() {
        store.updateDescription("Hello!")
        assertEquals("Hello!", store.signUpInfo.value.description)
    }

    @Test
    fun updatingOneFieldPreservesOtherFields() {
        store.updateFirstName("John")
        store.updateLastName("Doe")
        store.updateDescription("Hello!")

        assertEquals(SignUpInfo("John", "Doe", "Hello!"), store.signUpInfo.value)
    }

    @Test
    fun clearResetsSignUpInfo() {
        store.updateFirstName("John")
        store.updateLastName("Doe")
        store.updateDescription("Hello!")

        store.clear()

        assertEquals(SignUpInfo(), store.signUpInfo.value)
    }
}