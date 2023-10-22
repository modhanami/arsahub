package com.arsahub.backend

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIONamespace
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.ConnectListener
import com.corundumstudio.socketio.listener.DisconnectListener
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class SocketIOService(private val server: SocketIOServer) {
    private final val defaultNamespace: SocketIONamespace = server.addNamespace("/default")

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
        ) { client: SocketIOClient, data: String, ackSender: AckRequest ->
            println("subscribe-user: $data")
            client.joinRoom(getUserRoomName(data))
            ackSender.sendAckData("OK")
        }
    }

    fun broadcastToActivityRoom(activityId: Long, eventName: String, data: Any) {
        val activityRoom = defaultNamespace.getRoomOperations(getActivityRoomName(activityId))

        activityRoom.sendEvent(
            eventName, mapOf(
                "activityId" to activityId,
                "data" to data
            )
        )
    }

    fun broadcastToUserRoom(userId: String, eventName: String, data: Any) {
        val userRoom = defaultNamespace.getRoomOperations(getUserRoomName(userId))
        userRoom.sendEvent(
            eventName, mapOf(
                "userId" to userId,
                "data" to data
            )
        )
    }

    fun getActivityRoomName(activityId: Long): String {
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

