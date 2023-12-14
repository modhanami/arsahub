package com.arsahub.backend.services

import com.arsahub.backend.models.Activity

data class ActivityAddMembersResult(
    val activity: Activity,
    val hasNewMembers: Boolean
)