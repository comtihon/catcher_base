package com.catcher.base.config.security

import com.catcher.base.service.security.AppAuthSuccessHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler
import org.springframework.security.config.web.server.ServerHttpSecurity.http



@Configuration
@EnableResourceServer
class ResourceServerConfig(@Autowired val tokenServices: DefaultTokenServices,
                           @Autowired val successHandler: AppAuthSuccessHandler) : ResourceServerConfigurerAdapter() {
    override fun configure(resources: ResourceServerSecurityConfigurer) {
        resources.tokenServices(tokenServices)
    }

    override fun configure(http: HttpSecurity) {
//        http.anonymous().disable()
//                .authorizeRequests()
//                .antMatchers("/users/**").access("hasRole('ADMIN')")
//                .and().exceptionHandling().accessDeniedHandler(OAuth2AccessDeniedHandler())
        http.anonymous().disable()
                .requestMatchers()
                .and()
                .anonymous().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/v1/user/").permitAll() // register
                .antMatchers("/oauth/token").permitAll() // login
                .antMatchers("/api/v1/user/login").permitAll() // login
                .antMatchers(HttpMethod.DELETE, "/api/v1/team").hasAuthority("modify_teams")
                .antMatchers(HttpMethod.PUT, "/api/v1/team").hasAuthority("modify_teams")
                .antMatchers(HttpMethod.POST, "/api/v1/team").hasAuthority("modify_teams")
                .antMatchers(HttpMethod.DELETE, "/api/v1/project").hasAuthority("modify_projects")
                .antMatchers(HttpMethod.PUT, "/api/v1/project").hasAuthority("modify_projects")
                .antMatchers(HttpMethod.POST, "/api/v1/project").hasAuthority("modify_projects")
                .anyRequest().authenticated()
                .and()
                .formLogin().permitAll()
                .successHandler(successHandler)
    }
}