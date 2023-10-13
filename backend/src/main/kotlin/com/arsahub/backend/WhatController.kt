//package com.arsahub.backend
//
//import com.google.firebase.messaging.FirebaseMessaging
//import com.google.firebase.messaging.Message
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.RestController
//
//@RestController
//class WhatController(  private val firebaseMessaging: FirebaseMessaging) {
//    @GetMapping("/what")
//    // Generate test endpoint
//    fun what(): String {
//        val msg: Message = Message.builder()
//            .setToken("e6mwvZezejfxzG6HOS8c8W:APA91bEXTmwUhtQMGhcyF0377Yf_TQRFNuTKZ9yYSi86W7DsTPtk4j1spMoKnKCWjTDi9-w_sG-Oihy-MpfZCpgIdL7cGrkuE2XiujjqW9j45L7jSlQ_FH3I95h0-MnwvGDDWKxx4fp4")
//            .putData("body", "some data")
//            .build()
//
//        val messageId = firebaseMessaging.send(msg)
//        println("message sent: $messageId")
//        return messageId
//    }
//
//    @GetMapping("/what2")
//    fun what2(): String {
//        return "321"
//    }
//
//    @GetMapping("/what3")
//    fun what23(): String {
//        return "3dw21"
//    }
//
//    @GetMapping("/what4")
//    fun what24(): String {
//        return "3dwdw21"
//    }
//
//    @GetMapping("/what5")
//    fun what25(): String {
//        return "dwq"
//    }
//
//    @GetMapping("/what6")
//    fun what26(): String {
//        return "ds"
//    }
//
//    @GetMapping("/what7")
//    fun what27(): String {
//        return "dsdwqqw"
//    }
//
//    @GetMapping("/what8")
//    fun what28(): String {
//        return "dwq"
//    }
//
//    @GetMapping("/what9")
//    fun what29(): String {
//        return "dwwddwww"
//    }
//
//    @GetMapping("/what10")
//    fun what210(): String {
//        return "dwd23wddwww"
//    }
//
//    @GetMapping("/what11")
//    fun what211(): String {
//        return "321"
//    }
//}