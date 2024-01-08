package com.arsahub.backend.dtos.socketio

data class PointsUpdate(val userId: String, val points: Int) : AppUpdate
