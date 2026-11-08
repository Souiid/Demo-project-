package com.souiid.demoproject.firebase.data.repository.firebase

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.souiid.demoproject.firebase.data.repository.AuthRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuthException
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AuthRepositoryTest {

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val options = FirebaseOptions.Builder()
            .setApplicationId("1:123456789:android:test")
            .setApiKey("fake-api-key")
            .setProjectId("demoproject-316f5")
            .build()

        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context, options)
        }

        Firebase.auth.useEmulator("127.0.0.1", 9099)
        Firebase.firestore.useEmulator("10.0.2.2", 8080)
        runBlocking {
            Firebase.auth.signOut()
        }
    }

    @Test
    fun signInWithValidCredentials() = runBlocking {
        val repository = AuthRepository()

        repository.signIn("test@example.com", "password123")

        assertNotNull(Firebase.auth.currentUser)
        assertEquals("test@example.com", Firebase.auth.currentUser?.email)
    }

    @Test
    fun signInWithInvalidPassword() {
        runBlocking {
            val repository = AuthRepository()

            assertFailsWith<FirebaseAuthException> {
                repository.signIn("test@example.com", "wrongpassword")
            }
        }
    }

    @Test
    fun signInWithInvalidEmail() {
        runBlocking {
            val repository = AuthRepository()
            assertFailsWith<FirebaseAuthException> {
                repository.signIn("invalid-email", "password123")
            }
        }
    }

    @Test
    fun signInWithEmptyEmail() {
        runBlocking {
            val repository = AuthRepository()

            assertFailsWith<IllegalArgumentException> {
                repository.signIn("", "password123")
            }
        }
    }


    @Test
    fun signInWithUnknownEmail() {
        runBlocking {
            val repository = AuthRepository()
            assertFailsWith<FirebaseAuthException> {
                repository.signIn("unknown@example.com", "password123")
            }
        }
    }


    @Test
    fun signInWithEmptyPassword() {
        runBlocking {
            val repository = AuthRepository()

            assertFailsWith<IllegalArgumentException> {
                repository.signIn("test@example.com", "")
            }
        }
    }


    @Test
    fun signOutSuccessfully() {
        runBlocking {
            val repository = AuthRepository()

            repository.signIn("test@example.com", "password123")
            assertNotNull(Firebase.auth.currentUser)

            repository.signOut()

            assertNull(Firebase.auth.currentUser)
        }
    }
}