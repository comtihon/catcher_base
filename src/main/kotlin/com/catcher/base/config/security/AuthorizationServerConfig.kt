package com.catcher.base.config.security

import com.catcher.base.service.security.AppUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import javax.sql.DataSource


@Configuration
@EnableAuthorizationServer
class AuthorizationServerConfig(@Autowired @Qualifier("authenticationManagerBean")
                                val authenticationManager: AuthenticationManager,
                                @Autowired
                                val passwordEncoder: PasswordEncoder,
                                @Autowired
                                val userDetailsService: AppUserDetailsService,
                                @Autowired
                                val dataSource: DataSource) : AuthorizationServerConfigurerAdapter() {

    @Value("\${spring.security.oauth2.client.clientId}")
    private val clientId: String? = null

    @Value("\${spring.security.oauth2.client.clientSecret}")
    private val clientSecret: String? = null

    @Value("\${jwt.accessTokenValidititySeconds:43200}") // 12 hours
    private val accessTokenValiditySeconds: Int = 0

    @Value("\${jwt.authorizedGrantTypes:password,authorization_code,refresh_token}")
    private val authorizedGrantTypes: Array<String>? = null

    @Value("\${jwt.refreshTokenValiditySeconds:2592000}") // 30 days
    private val refreshTokenValiditySeconds: Int = 0

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints.accessTokenConverter(accessTokenConverter())
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .tokenStore(tokenStore())
    }

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.inMemory()
                .withClient(clientId)
                .secret(passwordEncoder.encode(clientSecret))
                .accessTokenValiditySeconds(accessTokenValiditySeconds)
                .refreshTokenValiditySeconds(refreshTokenValiditySeconds)
                .authorizedGrantTypes(*authorizedGrantTypes!!)
                .scopes("read", "write")
                .resourceIds("api")
    }

    @Bean
    fun tokenStore(): TokenStore {
        return JdbcTokenStore(dataSource)
    }

    @Bean
    fun accessTokenConverter(): JwtAccessTokenConverter = JwtAccessTokenConverter()
}