package com.souiid.demoproject.firebase.domain.usecase.user

import com.souiid.demoproject.firebase.domain.model.AppResult
import com.souiid.demoproject.firebase.domain.repository.IUserRepository

class UploadPhotoUseCase(private val userRepository: IUserRepository) {
    suspend operator fun invoke(uri: String, mimeType: String): AppResult<String> =
        userRepository.uploadPhoto(uri, mimeType)
}