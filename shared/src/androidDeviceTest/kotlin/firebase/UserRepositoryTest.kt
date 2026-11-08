package com.souiid.demoproject.firebase.data.repository.firebase

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.souiid.demoproject.firebase.data.repository.UserRepository
import com.souiid.demoproject.firebase.data.utils.FirebaseConstants
import com.souiid.demoproject.firebase.data.utils.followersCollection
import com.souiid.demoproject.firebase.data.utils.followingCollection
import com.souiid.demoproject.firebase.data.utils.userDocument
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.model.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.functions.functions
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.milliseconds

class UserRepositoryTest {

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
            Firebase.functions(FirebaseConstants.REGION).useEmulator("127.0.0.1", 5001)
        }
    }

    @Before
    fun setUp() {
        runBlocking {
            Firebase.auth.signOut()
        }
    }

    private suspend fun createTestUser(): String {
        val email = "test-${UUID.randomUUID()}@example.com"
        return requireNotNull(
            Firebase.auth.createUserWithEmailAndPassword(email, "password123").user
        ).uid
    }

    @Test
    fun fetchExistingUser() {
        runBlocking {
            val repository = UserRepository()
            val userId = UUID.randomUUID().toString()
            val user = User(userId, "John", "Doe", "Test profile")

            Firebase.firestore.userDocument(userId).set(user)

            val result = repository.fetchUser(userId)

            assertIs<AppResult.Success<User>>(result)
            assertEquals(user, result.data)
        }
    }

    @Test
    fun fetchNonExistingUser() {
        runBlocking {
            val repository = UserRepository()
            val userId = UUID.randomUUID().toString()

            val result = repository.fetchUser(userId)

            assertIs<AppResult.Failure>(result)
        }
    }

    @Test
    fun fetchOtherUserDoesNotUpdateCurrentUser() {
        runBlocking {
            val repository = UserRepository()
            val userId = UUID.randomUUID().toString()
            val user = User(userId, "Jane", "Doe", "Other profile")

            Firebase.firestore.userDocument(userId).set(user)

            val result = repository.fetchUser(userId)

            assertIs<AppResult.Success<User>>(result)
            assertEquals(user, result.data)
            assertNull(repository.currentUser.value)
        }
    }

    @Test
    fun updateUserSuccessfully() {
        runBlocking {
            val repository = UserRepository()
            val uid = createTestUser()
            val user = User(uid, "John", "Doe", "Old description")

            Firebase.firestore.userDocument(uid).set(user)
            repository.fetchUser(uid)

            val result = repository.updateUser("Jane", "Smith", "New description", null)

            assertIs<AppResult.Success<Unit>>(result)
            assertEquals("Jane", repository.currentUser.value?.firstName)
            assertEquals("Smith", repository.currentUser.value?.lastName)
            assertEquals("New description", repository.currentUser.value?.description)

            val savedUser = Firebase.firestore.userDocument(uid).get().data<User>()
            assertEquals("Jane", savedUser.firstName)
            assertEquals("Smith", savedUser.lastName)
            assertEquals("New description", savedUser.description)
        }
    }

    @Test
    fun updateUserWithoutLoadedProfile() {
        runBlocking {
            val repository = UserRepository()

            val result = repository.updateUser("Jane", "Smith", "Description", null)

            assertIs<AppResult.Failure>(result)
        }
    }

    @Test
    fun updateUserKeepsExistingPhoto() {
        runBlocking {
            val repository = UserRepository()
            val uid = createTestUser()
            val user = User(uid, "John", "Doe", "Description", "https://example.com/photo.jpg")

            Firebase.firestore.userDocument(uid).set(user)
            repository.fetchUser(uid)

            val result = repository.updateUser("Jane", "Doe", "Updated", null)

            assertIs<AppResult.Success<Unit>>(result)
            assertEquals(user.photoUrl, repository.currentUser.value?.photoUrl)

            val savedUser = Firebase.firestore.userDocument(uid).get().data<User>()
            assertEquals(user.photoUrl, savedUser.photoUrl)
        }
    }

    @Test
    fun searchUsersByFirstName() {
        runBlocking {
            val repository = UserRepository()
            val id = UUID.randomUUID().toString()
            val user = User(id, "UniqueAlex${id.take(8)}", "Martin", "Profile")

            Firebase.firestore.userDocument(id).set(user)

            val result = repository.searchUsers(user.firstName)

            assertIs<AppResult.Success<List<User>>>(result)
            assertTrue(result.data.any { it.id == id })
        }
    }

    @Test
    fun searchUsersByLastName() {
        runBlocking {
            val repository = UserRepository()
            val id = UUID.randomUUID().toString()
            val user = User(id, "John", "UniqueSmith${id.take(8)}", "Profile")

            Firebase.firestore.userDocument(id).set(user)

            val result = repository.searchUsers(user.lastName)

            assertIs<AppResult.Success<List<User>>>(result)
            assertTrue(result.data.any { it.id == id })
        }
    }

    @Test
    fun searchUsersByFullName() {
        runBlocking {
            val repository = UserRepository()
            val id = UUID.randomUUID().toString()
            val user = User(id, "Alex${id.take(8)}", "Martin", "Profile")

            Firebase.firestore.userDocument(id).set(user)

            val result = repository.searchUsers("${user.firstName} ${user.lastName}")

            assertIs<AppResult.Success<List<User>>>(result)
            assertTrue(result.data.any { it.id == id })
        }
    }

    @Test
    fun searchUsersWithBlankQuery() {
        runBlocking {
            val repository = UserRepository()

            val result = repository.searchUsers("   ")

            assertIs<AppResult.Success<List<User>>>(result)
            assertTrue(result.data.isEmpty())
        }
    }

    @Test
    fun searchUsersExcludesCurrentUser() {
        runBlocking {
            val repository = UserRepository()
            val uid = createTestUser()
            val user = User(uid, "UniqueCurrentUser", "Tester", "Profile")

            Firebase.firestore.userDocument(uid).set(user)

            val result = repository.searchUsers("UniqueCurrentUser")

            assertIs<AppResult.Success<List<User>>>(result)
            assertTrue(result.data.none { it.id == uid })
        }
    }

    @Test
    fun getFollowingUserIdsSuccessfully() {
        runBlocking {
            val repository = UserRepository()
            val userId = UUID.randomUUID().toString()
            val targetId1 = UUID.randomUUID().toString()
            val targetId2 = UUID.randomUUID().toString()

            Firebase.firestore.followingCollection(userId).document(targetId1).set(mapOf("createdAt" to 1))
            Firebase.firestore.followingCollection(userId).document(targetId2).set(mapOf("createdAt" to 1))

            val result = repository.getFollowingUserIds(userId)

            assertIs<AppResult.Success<Set<String>>>(result)
            assertEquals(setOf(targetId1, targetId2), result.data)
        }
    }

    @Test
    fun getFollowerUserIdsSuccessfully() {
        runBlocking {
            val repository = UserRepository()
            val userId = UUID.randomUUID().toString()
            val followerId = UUID.randomUUID().toString()

            Firebase.firestore.followersCollection(userId).document(followerId).set(mapOf("createdAt" to 1))

            val result = repository.getFollowerUserIds(userId)

            assertIs<AppResult.Success<Set<String>>>(result)
            assertEquals(setOf(followerId), result.data)
        }
    }

    @Test
    fun getFollowingUsersSuccessfully() {
        runBlocking {
            val repository = UserRepository()
            val userId = UUID.randomUUID().toString()
            val targetId = UUID.randomUUID().toString()
            val targetUser = User(targetId, "Jane", "Smith", "Profile")

            Firebase.firestore.userDocument(targetId).set(targetUser)
            Firebase.firestore.followingCollection(userId).document(targetId).set(mapOf("createdAt" to 1))

            val result = repository.getFollowingUsers(userId)

            assertIs<AppResult.Success<List<User>>>(result)
            assertEquals(listOf(targetUser), result.data)
        }
    }

    @Test
    fun getFollowingUsersSkipsMissingProfiles() {
        runBlocking {
            val repository = UserRepository()
            val userId = UUID.randomUUID().toString()
            val missingId = UUID.randomUUID().toString()

            Firebase.firestore.followingCollection(userId).document(missingId).set(mapOf("createdAt" to 1))

            val result = repository.getFollowingUsers(userId)

            assertIs<AppResult.Success<List<User>>>(result)
            assertTrue(result.data.isEmpty())
        }
    }

    @Test
    fun observeFollowingUserIds() {
        runBlocking {
            val repository = UserRepository()
            val userId = UUID.randomUUID().toString()
            val targetId = UUID.randomUUID().toString()

            Firebase.firestore.followingCollection(userId).document(targetId).set(mapOf("createdAt" to 1))

            val result = withTimeout(5000.milliseconds) {
                repository.observeFollowingUserIds(userId).first { targetId in it }
            }

            assertTrue(targetId in result)
        }
    }

    @Test
    fun observeFollowerUserIds() {
        runBlocking {
            val repository = UserRepository()
            val userId = UUID.randomUUID().toString()
            val followerId = UUID.randomUUID().toString()

            Firebase.firestore.followersCollection(userId).document(followerId).set(mapOf("createdAt" to 1))

            val result = withTimeout(5000.milliseconds) {
                repository.observeFollowerUserIds(userId).first { followerId in it }
            }

            assertTrue(followerId in result)
        }
    }
}