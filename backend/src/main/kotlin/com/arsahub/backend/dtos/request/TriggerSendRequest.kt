package com.arsahub.backend.dtos.request

data class TriggerSendRequest(val key: String, val params: Map<String, String>?, val userId: String)