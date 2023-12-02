package com.arsahub.backend

import com.arsahub.backend.dtos.AchievementUnlock
import com.arsahub.backend.dtos.ActivityUpdate
import com.arsahub.backend.dtos.LeaderboardUpdate
import com.arsahub.backend.dtos.PointsUpdate
import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIONamespace
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.ConnectListener
import com.corundumstudio.socketio.listener.DisconnectListener
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class SocketIOService(val server: SocketIOServer) {
    private final val defaultNamespace: SocketIONamespace by lazy { server.addNamespace("/default") }

    init {
        defaultNamespace.addConnectListener(onConnected())
        defaultNamespace.addDisconnectListener(onDisconnected())
        defaultNamespace.addEventListener(
            "subscribe-activity",
            Long::class.java
        ) { client: SocketIOClient, data: Long, ackSender: AckRequest ->
            println("subscribe-activity: $data")
            client.joinRoom(getActivityRoomName(data))
            ackSender.sendAckData("OK")
        }
        defaultNamespace.addEventListener(
            "subscribe-user",
            String::class.java
        ) { client: SocketIOClient, rawUserId: String, ackSender: AckRequest ->
            println("subscribe-user: $rawUserId")
            val userId = rawUserId.toLongOrNull()
            if (userId == null) {
                ackSender.sendAckData("Invalid user ID")
                return@addEventListener
            }
            client.joinRoom(getUserRoomName(userId))
            ackSender.sendAckData("OK")
        }
    }

    fun broadcastToActivityRoom(activityId: Long, data: ActivityUpdate) {
        val activityRoom = defaultNamespace.getRoomOperations(getActivityRoomName(activityId))
        val type = when (data) {
            is PointsUpdate -> "points-update"
            is LeaderboardUpdate -> "leaderboard-update"
            is AchievementUnlock -> "achievement-unlock"
            else -> throw IllegalArgumentException("Unknown data type: ${data.javaClass}")
        }
        activityRoom.sendEvent(
            "activity-update", mapOf(
                "type" to type,
                "activityId" to activityId,
                "data" to data
            )
        )
        println("broadcastToActivityRoom: $activityId, $data")
    }

    fun broadcastToUserRoom(userId: Long, data: ActivityUpdate) {
        val userRoom = defaultNamespace.getRoomOperations(getUserRoomName(userId))
        val type = when (data) {
            is PointsUpdate -> "points-update"
            is LeaderboardUpdate -> "leaderboard-update"
            is AchievementUnlock -> "achievement-unlock"
            else -> throw IllegalArgumentException("Unknown data type: ${data.javaClass}")
        }
        userRoom.sendEvent(
            "user-update", mapOf(
                "type" to type,
                "userId" to userId,
                "data" to data
            )
        )
        println("broadcastToUserRoom: $userId, $data")
    }

    fun getActivityRoomName(activityId: Long): String {
        return "/activities/$activityId"
    }

    fun getUserRoomName(userId: Long): String {
        return "/users/$userId"
    }

    private fun onConnected(): ConnectListener {
        return ConnectListener { client: SocketIOClient ->
            val handshakeData = client.handshakeData
            log.debug(
                "Client[{}] - Connected to socket.io service through '{}'",
                client.sessionId.toString(),
                handshakeData.url
            )
        }
    }

    private fun onDisconnected(): DisconnectListener {
        return DisconnectListener { client: SocketIOClient ->
            log.debug(
                "Client[{}] - Disconnected from socket.io service.",
                client.sessionId.toString()
            )
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(SocketIOService::class.java)
    }
}

