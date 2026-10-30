package com.souiid.demoproject.firebase.domain.repository

import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface IPostRepository {
    suspend fun createPost(content: String): AppResult<Unit>
    suspend fun getFeedPosts(authorIds: List<String>): AppResult<List<Post>>
    suspend fun getUserPosts(userId: String): AppResult<List<Post>>
    suspend fun likePost(postId: String): AppResult<Unit>
    suspend fun unlikePost(postId: String): AppResult<Unit>
    suspend fun getLikedPostIds(postIds: List<String>): AppResult<Set<String>>
    fun observeFeedPosts(authorIds: List<String>): Flow<List<Post>>
    suspend fun deletePost(postId: String): AppResult<Unit>
}