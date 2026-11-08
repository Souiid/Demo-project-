package com.souiid.demoproject.firebase.data.repository.firebase

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.souiid.demoproject.firebase.data.repository.PostRepository
import com.souiid.demoproject.firebase.data.utils.FirebaseConstants
import com.souiid.demoproject.firebase.data.utils.likesCollection
import com.souiid.demoproject.firebase.data.utils.postsCollection
import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.model.Post
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.functions.functions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class PostRepositoryTest {

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

    private suspend fun createTestPost(
        authorId: String,
        content: String = "Test post",
        likesCount: Int = 0
    ): String {
        val postId = UUID.randomUUID().toString()

        Firebase.firestore.postsCollection().document(postId).set(
            mapOf(
                FirebaseConstants.Fields.AUTHOR_ID to authorId,
                FirebaseConstants.Fields.CONTENT to content,
                FirebaseConstants.Fields.CREATED_AT to Timestamp.now(),
                FirebaseConstants.Fields.LIKES_COUNT to likesCount
            )
        )

        return postId
    }

    @Test
    fun getFeedPostsWithEmptyAuthors() {
        runBlocking {
            val repository = PostRepository()

            val result = repository.getFeedPosts(emptyList())

            assertIs<AppResult.Success<List<Post>>>(result)
            assertTrue(result.data.isEmpty())
        }
    }

    @Test
    fun getFeedPostsSuccessfully() {
        runBlocking {
            val repository = PostRepository()
            val userId = createTestUser()
            val firstPostId = createTestPost(userId, "First post")
            val secondPostId = createTestPost(userId, "Second post")

            val result = repository.getFeedPosts(listOf(userId))

            assertIs<AppResult.Success<List<Post>>>(result)
            assertEquals(2, result.data.size)
            assertEquals(setOf(firstPostId, secondPostId), result.data.map { it.id }.toSet())
            assertTrue(result.data.all { it.authorId == userId })
        }
    }

    @Test
    fun getUserPostsSuccessfully() {
        runBlocking {
            val repository = PostRepository()
            val userId = createTestUser()
            val postId = createTestPost(userId, "My post")

            val result = repository.getUserPosts(userId)

            assertIs<AppResult.Success<List<Post>>>(result)
            assertEquals(1, result.data.size)
            assertEquals(postId, result.data.first().id)
            assertEquals("My post", result.data.first().content)
        }
    }

    @Test
    fun getLikedPostIdsSuccessfully() {
        runBlocking {
            val repository = PostRepository()
            val userId = createTestUser()
            val firstPostId = createTestPost(userId)
            val secondPostId = createTestPost(userId)

            Firebase.firestore.likesCollection(firstPostId).document(userId)
                .set(mapOf(FirebaseConstants.Fields.CREATED_AT to Timestamp.now()))

            val result = repository.getLikedPostIds(listOf(firstPostId, secondPostId))

            assertIs<AppResult.Success<Set<String>>>(result)
            assertEquals(setOf(firstPostId), result.data)
        }
    }

    @Test
    fun getLikedPostIdsWithEmptyList() {
        runBlocking {
            val repository = PostRepository()
            createTestUser()

            val result = repository.getLikedPostIds(emptyList())

            assertIs<AppResult.Success<Set<String>>>(result)
            assertTrue(result.data.isEmpty())
        }
    }

    @Test
    fun observeFeedPostsSuccessfully() {
        runBlocking {
            val repository = PostRepository()
            val userId = createTestUser()
            val postId = createTestPost(userId, "Observed post")

            val posts = withTimeout(5000.milliseconds) {
                repository.observeFeedPosts(listOf(userId)).first { items ->
                    items.any { it.id == postId }
                }
            }

            assertEquals(1, posts.size)
            assertEquals("Observed post", posts.first().content)
        }
    }

    @Test
    fun observeUserPostsSuccessfully() {
        runBlocking {
            val repository = PostRepository()
            val userId = createTestUser()
            val postId = createTestPost(userId, "User observed post")

            val posts = withTimeout(5000.milliseconds) {
                repository.observeUserPosts(userId).first { items ->
                    items.any { it.id == postId }
                }
            }

            assertEquals(1, posts.size)
            assertEquals("User observed post", posts.first().content)
        }
    }

    @Test
    fun observeFeedPostsWithEmptyAuthors() {
        runBlocking {
            val repository = PostRepository()

            val posts = repository.observeFeedPosts(emptyList()).first()

            assertTrue(posts.isEmpty())
        }
    }
}