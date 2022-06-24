package com.catcher.base.config.security

import com.catcher.base.service.security.AppUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain


@Configuration
//@EnableWebSecurity TODO
class SecurityConfig(@Autowired val userDetailsService: AppUserDetailsService) {

//    @Bean
//    @Throws(Exception::class)
//    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
//        // @formatter:off
//        http
//            .authorizeHttpRequests { authorize ->
//                authorize
//                    .and()
//                    .requestMatchers().antMatchers("/api/v1/**")
//                    .and()
//                    .authorizeRequests()
//                    .antMatchers("/**", "/login**", "/oauth/error").permitAll()
//                    .antMatchers(HttpMethod.POST, "/api/v1/user").permitAll() // register
//                    .antMatchers(HttpMethod.DELETE, "/api/v1/team").hasAuthority("modify_teams")
//                    .antMatchers(HttpMethod.PUT, "/api/v1/team").hasAuthority("modify_teams")
//                    .antMatchers(HttpMethod.POST, "/api/v1/team").hasAuthority("modify_teams")
//                    .antMatchers(HttpMethod.DELETE, "/api/v1/project").hasAuthority("modify_projects")
//                    .antMatchers(HttpMethod.PUT, "/api/v1/project").hasAuthority("modify_projects")
//                    .antMatchers(HttpMethod.POST, "/api/v1/project").hasAuthority("modify_projects")
//                    .antMatchers(HttpMethod.GET, "/api/v1/test/run").hasAuthority("launch_tests")
//                    .antMatchers(HttpMethod.POST, "/api/v1/project/{\\d+}/add_test").hasAuthority("modify_tests")
//                    .antMatchers(HttpMethod.DELETE, "/api/v1/project/{\\d+}/del_test/**").hasAuthority("modify_tests")
//                    .antMatchers(HttpMethod.PUT, "/api/v1/test").hasAuthority("modify_tests")
//                    .anyRequest().authenticated()
//            }
//            .oauth2ResourceServer { obj: OAuth2ResourceServerConfigurer<HttpSecurity?> -> obj.jwt() }
//        // @formatter:on
//        return http.build()
//    }

    @Bean
    fun encoder(): PasswordEncoder = BCryptPasswordEncoder(11)
}