package com.arsahub.backend.dtos

data class ActivityTriggerRequest(val key: String, val params: Map<String, String>?, val userId: String)