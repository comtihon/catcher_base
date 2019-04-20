package com.catcher.base.config.security

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer

@Configuration
@EnableResourceServer
class ResourceServer : ResourceServerConfigurerAdapter() {

    override fun configure(resources: ResourceServerSecurityConfigurer) {
        resources.resourceId("api")
    }

    override fun configure(http: HttpSecurity) {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/v1/user").permitAll() // register
                .antMatchers(HttpMethod.DELETE, "/api/v1/team").hasAuthority("modify_teams")
                .antMatchers(HttpMethod.PUT, "/api/v1/team").hasAuthority("modify_teams")
                .antMatchers(HttpMethod.POST, "/api/v1/team").hasAuthority("modify_teams")
                .antMatchers(HttpMethod.DELETE, "/api/v1/project").hasAuthority("modify_projects")
                .antMatchers(HttpMethod.PUT, "/api/v1/project").hasAuthority("modify_projects")
                .antMatchers(HttpMethod.POST, "/api/v1/project").hasAuthority("modify_projects")
                .anyRequest().authenticated()
        // TODO per project access rights?
    }
}