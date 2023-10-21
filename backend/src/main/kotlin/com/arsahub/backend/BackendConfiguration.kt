package com.arsahub.backend

import org.h2.tools.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class BackendConfiguration {
//    @Bean
//    fun firebaseMessaging(firebaseApp: FirebaseApp): FirebaseMessaging {
//        return FirebaseMessaging.getInstance(firebaseApp)
//    }
//
//    @Bean
//    fun firebaseApp(credentials: GoogleCredentials): FirebaseApp {
//        val options: FirebaseOptions = FirebaseOptions.builder()
//            .setCredentials(credentials)
//            .build()
//        return FirebaseApp.initializeApp(options)
//    }
//
//    @Bean
//    fun googleCredentials(): GoogleCredentials {
//        return firebaseProperties.serviceAccount?.let {
//            GoogleCredentials.fromStream(it.inputStream())
//        } ?: GoogleCredentials.getApplicationDefault()
//    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    fun h2Server(): Server {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092")
    }
}