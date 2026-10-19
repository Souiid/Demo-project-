package com.souiid.demoproject.domain

import com.souiid.demoproject.data.SampleRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveUserNameUseCase(
    private val sampleRepository: SampleRepository
) {
    operator fun invoke(): StateFlow<String?> = sampleRepository.userName
}