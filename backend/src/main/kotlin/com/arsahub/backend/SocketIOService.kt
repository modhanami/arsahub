package com.arsahub.backend

import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIONamespace
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.ConnectListener
import com.corundumstudio.socketio.listener.DisconnectListener
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class SocketIOService(private val server: SocketIOServer) {
    private val activityNamespaces: MutableMap<Long, SocketIONamespace> = mutableMapOf()
    private val userNamespaces: MutableMap<String, SocketIONamespace> = mutableMapOf()

    init {
        getOrCreateNamespaceForActivity(1)
    }

    private fun createNamespaceForActivity(activityId: Long): SocketIONamespace {
        val activityNamespace: SocketIONamespace = server.addNamespace(getNamespaceNameForActivity(activityId))
        activityNamespace.addConnectListener(onConnected())
        activityNamespace.addDisconnectListener(onDisconnected())

        return activityNamespace
    }

    final fun getOrCreateNamespaceForActivity(activityId: Long): SocketIONamespace {
        return activityNamespaces.getOrPut(activityId) { createNamespaceForActivity(activityId) }
    }

    fun getActivityNamespace(activityId: Long): SocketIONamespace? {
        return activityNamespaces[activityId]
    }

    final fun getOrCreateNamespaceForUser(userId: String): SocketIONamespace {
        return userNamespaces.getOrPut(userId) { createNamespaceForUser(userId) }
    }

    fun getUserNamespace(userId: String): SocketIONamespace? {
        return userNamespaces[userId]
    }

    private fun createNamespaceForUser(userId: String): SocketIONamespace {
        val userNamespace: SocketIONamespace = server.addNamespace(getNamespaceNameForUser(userId))
        userNamespace.addConnectListener(onConnected())
        userNamespace.addDisconnectListener(onDisconnected())

        return userNamespace
    }

    fun getNamespaceNameForActivity(activityId: Long): String {
        return "/activities/$activityId"
    }

    fun getNamespaceNameForUser(userId: String): String {
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

