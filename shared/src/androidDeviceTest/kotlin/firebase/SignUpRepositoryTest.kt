package com.souiid.demoproject.firebase.data.repository.firebase

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.souiid.demoproject.firebase.data.repository.SignUpRepository
import com.souiid.demoproject.firebase.data.utils.SignUpInfoStore
import com.souiid.demoproject.firebase.data.utils.userDocument
import com.souiid.demoproject.firebase.domain.model.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuthException
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SignUpRepositoryTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun setUpFirebase() {
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
            Firebase.firestore.useEmulator("127.0.0.1", 8080)
        }
    }

    @Before
    fun setUp() {
        runBlocking {
            Firebase.auth.signOut()
        }
    }

    @Test
    fun signUpSuccessfully() {
        runBlocking {
            val repository = SignUpRepository(SignUpInfoStore())
            val email = "signup-${UUID.randomUUID()}@example.com"

            repository.updateFirstName("John")
            repository.updateLastName("Doe")
            repository.updateDescription("Test profile")

            repository.signUp(email, "password123")

            assertNotNull(Firebase.auth.currentUser)
        }
    }

    @Test
    fun signUpCreatesUserDocument() {
        runBlocking {
            val repository = SignUpRepository(SignUpInfoStore())
            val email = "signup-${UUID.randomUUID()}@example.com"

            repository.updateFirstName("John")
            repository.updateLastName("Doe")
            repository.updateDescription("Test profile")
            repository.signUp(email, "password123")

            val uid = assertNotNull(Firebase.auth.currentUser).uid
            val document = Firebase.firestore.userDocument(uid).get()

            assertTrue(document.exists)
            val user = document.data<User>()
            assertEquals(uid, user.id)
            assertEquals("John", user.firstName)
            assertEquals("Doe", user.lastName)
            assertEquals("Test profile", user.description)
        }
    }

    @Test
    fun signUpWithExistingEmail() {
        runBlocking {
            val repository = SignUpRepository(SignUpInfoStore())

            assertFailsWith<FirebaseAuthException> {
                repository.signUp("test@example.com", "password123")
            }
        }
    }

    @Test
    fun signUpWithInvalidEmail() {
        runBlocking {
            val repository = SignUpRepository(SignUpInfoStore())

            assertFailsWith<FirebaseAuthException> {
                repository.signUp("invalid-email", "password123")
            }
        }
    }

    @Test
    fun signUpWithEmptyEmail() {
        runBlocking {
            val repository = SignUpRepository(SignUpInfoStore())

            assertFailsWith<IllegalArgumentException> {
                repository.signUp("", "password123")
            }
        }
    }

    @Test
    fun signUpWithEmptyPassword() {
        runBlocking {
            val repository = SignUpRepository(SignUpInfoStore())

            assertFailsWith<IllegalArgumentException> {
                repository.signUp("signup-empty-password@example.com", "")
            }
        }
    }

    @Test
    fun signUpClearsInfoAfterSuccess() {
        runBlocking {
            val repository = SignUpRepository(SignUpInfoStore())
            val email = "signup-${UUID.randomUUID()}@example.com"

            repository.updateFirstName("John")
            repository.updateLastName("Doe")
            repository.updateDescription("Test profile")

            repository.signUp(email, "password123")

            assertEquals("", repository.signUpInfo.value.firstName)
            assertEquals("", repository.signUpInfo.value.lastName)
            assertEquals("", repository.signUpInfo.value.description)
        }
    }
}