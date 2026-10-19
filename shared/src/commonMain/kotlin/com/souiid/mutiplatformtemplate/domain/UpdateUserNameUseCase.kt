package com.souiid.mutiplatformtemplate.domain

import com.souiid.mutiplatformtemplate.data.SampleRepository

class UpdateUserNameUseCase(
    private val sampleRepository: SampleRepository
) {
     operator fun invoke(name: String) = sampleRepository.updateUserName(name)
}