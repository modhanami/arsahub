package com.arsahub.backend

import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Component

@Component
class MyGenericTask(
//    private val firebaseProperties: FirebaseProperties,
//    private val firebaseMessaging: FirebaseMessaging
    private val jwtEncoder: JwtEncoder,
    private val jwtDecoder: JwtDecoder
) : ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        println("Application started")
        val token = jwtEncoder.encode(
            JwtEncoderParameters.from(
                JwtClaimsSet.builder()
                    .claim("userId", 1)
                    .claim("username", "admin")
                    .claim("role", "admin")
                    .build()
            )
        )

        println("token: ${token.tokenValue}")

        // verify

        val decoded = jwtDecoder.decode(token.tokenValue)
        println("decoded: ${decoded.claims}")



//        val msg: Message = Message.builder()
//            .setToken("e6mwvZezejfxzG6HOS8c8W:APA91bEXTmwUhtQMGhcyF0377Yf_TQRFNuTKZ9yYSi86W7DsTPtk4j1spMoKnKCWjTDi9-w_sG-Oihy-MpfZCpgIdL7cGrkuE2XiujjqW9j45L7jSlQ_FH3I95h0-MnwvGDDWKxx4fp4")
//            .putData("body", "some data")
//            .build()
//
//        val send = firebaseMessaging.send(msg)
//        println("message sent: $send")
    }
}
