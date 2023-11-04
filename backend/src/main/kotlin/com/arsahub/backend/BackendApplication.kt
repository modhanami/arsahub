package com.arsahub.backend

import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOServer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
class BackendApplication {
    @Value("\${socketio.server.host}")
    val host: String? = null

    @Value("\${socketio.server.port}")
    private var port: Int = 0

    @Bean
    fun socketIOServer(): SocketIOServer {
        val config = Configuration()
        config.hostname = host
        config.port = port
        config.socketConfig.isReuseAddress = true
        return SocketIOServer(config)
    }

    @EventListener(ApplicationReadyEvent::class)
    fun startup() {
        val socketIOServer = socketIOServer()
        socketIOServer.start()
    }
}

fun main(args: Array<String>) {
    runApplication<BackendApplication>(*args)
}
