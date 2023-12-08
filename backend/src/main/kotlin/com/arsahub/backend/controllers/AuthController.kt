//package com.arsahub.backend.controllers
//
//import com.arsahub.backend.utils.JwtUtil
//import org.springframework.security.authentication.AuthenticationManager
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
//import org.springframework.web.bind.annotation.PostMapping
//import org.springframework.web.bind.annotation.RequestBody
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RestController
//
//@RestController
//@RequestMapping("/api/auth")
//class AuthController(
//    private val authenticationManager: AuthenticationManager,
//    private val jwtUtil: JwtUtil
//) {
//    data class LoginRequest(val username: String, val password: String)
//
//    data class LoginResponse(val token: String)
//
//    @PostMapping("/login")
//    fun login(@RequestBody loginRequest: LoginRequest): LoginResponse {
//        val authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(
//            loginRequest.username,
//            loginRequest.password
//        )
//        val authentication = authenticationManager.authenticate(authenticationRequest)
//        val token = jwtUtil.generateToken(loginRequest.username)
//        return LoginResponse(token)
//    }
//}