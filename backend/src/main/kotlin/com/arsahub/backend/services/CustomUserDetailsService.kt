//package com.arsahub.backend.services
//
//import com.arsahub.backend.repositories.UserRepository
//import org.springframework.security.core.userdetails.UserDetails
//import org.springframework.security.core.userdetails.UserDetailsService
//import org.springframework.security.core.userdetails.UsernameNotFoundException
//import org.springframework.stereotype.Service
//
//@Service
//class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {
//
//    override fun loadUserByUsername(username: String): UserDetails {
//        val user = userRepository.findByUsername(username)
//            ?: throw UsernameNotFoundException("User not found with username: $username")
//
//        return CustomUserDetails(
//            userId = user.userId,
//            username = user.username,
//            password = "password",
//            authorities = listOf(user.role.toAuthority()),
//            role = user.role
//        )
//    }
//}