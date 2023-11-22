package com.arsahub.backend

import org.h2.tools.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class BackendConfiguration {
    @Bean(initMethod = "start", destroyMethod = "stop")
    fun h2Server(): Server {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092")
    }
}