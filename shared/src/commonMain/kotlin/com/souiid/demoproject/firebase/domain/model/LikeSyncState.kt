package com.souiid.demoproject.firebase.domain.model

import kotlinx.coroutines.Job

class LikeSyncState(
    var confirmedLiked: Boolean,
    var syncing: Boolean = false,
    var debounceJob: Job? = null,
    var revision: Int = 0
)