package com.souiid.demoproject.domain

import com.souiid.demoproject.data.SampleRepository

class UpdateUserNameUseCase(
    private val sampleRepository: SampleRepository
) {
     operator fun invoke(name: String) = sampleRepository.updateUserName(name)
}