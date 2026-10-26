package com.souiid.demoproject.domain.repository

import com.souiid.demoproject.domain.model.AppResult
import com.souiid.demoproject.domain.model.Post

interface IPostRepository {
    suspend fun createPost(content: String): AppResult<Unit>
    suspend fun getFeedPosts(authorIds: List<String>): AppResult<List<Post>>
    suspend fun getUserPosts(userId: String): AppResult<List<Post>>
    suspend fun likePost(postId: String): AppResult<Unit>
    suspend fun unlikePost(postId: String): AppResult<Unit>
    suspend fun getLikedPostIds(postIds: List<String>): AppResult<Set<String>>
}