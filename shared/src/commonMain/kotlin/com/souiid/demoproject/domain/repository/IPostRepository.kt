package com.souiid.demoproject.domain.repository

import com.souiid.demoproject.domain.model.AppResult

interface IPostRepository {
    suspend fun createPost(content: String): AppResult<Unit>
}