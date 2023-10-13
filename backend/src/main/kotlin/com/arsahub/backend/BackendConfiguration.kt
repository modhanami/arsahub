//package com.arsahub.backend
//
//import com.google.auth.oauth2.GoogleCredentials
//import com.google.firebase.FirebaseApp
//import com.google.firebase.FirebaseOptions
//import com.google.firebase.messaging.FirebaseMessaging
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//
//
//@Configuration
//class BackendConfiguration(private val firebaseProperties: FirebaseProperties) {
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
//}