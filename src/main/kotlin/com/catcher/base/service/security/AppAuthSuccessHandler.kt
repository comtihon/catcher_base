package com.catcher.base.service.security

import java.io.IOException
import com.catcher.base.data.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class AppAuthSuccessHandler(@Autowired val userRepository: UserRepository) : AuthenticationSuccessHandler {

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(arg0: HttpServletRequest, arg1: HttpServletResponse, arg2: Authentication) {
        userRepository.updateLastLogin(LocalDateTime.now())
    }

}