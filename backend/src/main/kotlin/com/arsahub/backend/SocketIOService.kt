package com.arsahub.backend

import com.arsahub.backend.dtos.socketio.AchievementUnlock
import com.arsahub.backend.dtos.socketio.AppUpdate
import com.arsahub.backend.dtos.socketio.LeaderboardUpdate
import com.arsahub.backend.dtos.socketio.PointsUpdate
import com.arsahub.backend.models.App
import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.ConnectListener
import com.corundumstudio.socketio.listener.DisconnectListener
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SocketIOService(private val server: SocketIOServer) {
    init {
        server.addConnectListener(onConnected())
        server.addDisconnectListener(onDisconnected())
        server.addEventListener(
            "subscribe-activity",
            Long::class.java,
        ) { client: SocketIOClient, data: Long, ackSender: AckRequest ->
            println("subscribe-activity: $data")
            client.joinRoom(getAppRoomName(data))
            ackSender.sendAckData("OK")
        }
        server.addEventListener(
            "subscribe-user",
            String::class.java,
        ) { client: SocketIOClient, rawUserId: String?, ackSender: AckRequest ->
            println("subscribe-user: $rawUserId")
            val userId = rawUserId?.trim()
            if (userId.isNullOrEmpty()) {
                ackSender.sendAckData("Invalid user ID")
            } else {
                client.joinRoom(getUserRoomName(userId))
                ackSender.sendAckData("OK")
            }
        }
    }

    fun broadcastToAppRoom(
        app: App,
        data: AppUpdate,
    ) {
        val appId = app.id!!
        val activityRoom = server.getRoomOperations(getAppRoomName(appId))
        val type =
            when (data) {
                is PointsUpdate -> "points-update"
                is LeaderboardUpdate -> "leaderboard-update"
                is AchievementUnlock -> "achievement-unlock"
                else -> throw IllegalArgumentException("Unknown data type: ${data.javaClass}")
            }
        activityRoom.sendEvent(
            "activity-update",
            mapOf(
                "type" to type,
                "appId" to appId,
                "data" to data,
            ),
        )
        println("broadcastToActivityRoom: $appId, $data")
    }

    fun broadcastToUserRoom(
        userId: String,
        data: AppUpdate,
    ) {
        val userRoom = server.getRoomOperations(getUserRoomName(userId))
        val type =
            when (data) {
                is PointsUpdate -> "points-update"
                is LeaderboardUpdate -> "leaderboard-update"
                is AchievementUnlock -> "achievement-unlock"
                else -> throw IllegalArgumentException("Unknown data type: ${data.javaClass}")
            }
        userRoom.sendEvent(
            "user-update",
            mapOf(
                "type" to type,
                "userId" to userId,
                "data" to data,
            ),
        )
        println("broadcastToUserRoom: $userId, $data")
    }

    fun getAppRoomName(activityId: Long): String {
        return "/activities/$activityId"
    }

    fun getUserRoomName(userId: String): String {
        return "/users/$userId"
    }

    private fun onConnected(): ConnectListener {
        return ConnectListener { client: SocketIOClient ->
            val handshakeData = client.handshakeData
            log.debug(
                "Client[{}] - Connected to socket.io service through '{}'",
                client.sessionId.toString(),
                handshakeData.url,
            )
        }
    }

    private fun onDisconnected(): DisconnectListener {
        return DisconnectListener { client: SocketIOClient ->
            log.debug(
                "Client[{}] - Disconnected from socket.io service.",
                client.sessionId.toString(),
            )
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(SocketIOService::class.java)
    }
}
