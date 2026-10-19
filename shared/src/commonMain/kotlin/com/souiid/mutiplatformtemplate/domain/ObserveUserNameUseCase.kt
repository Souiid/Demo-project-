package com.souiid.mutiplatformtemplate.domain

import com.souiid.mutiplatformtemplate.data.SampleRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveUserNameUseCase(
    private val sampleRepository: SampleRepository
) {
    operator fun invoke(): StateFlow<String?> = sampleRepository.userName
}