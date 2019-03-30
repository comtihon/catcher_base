package com.catcher.base.config.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.oauth2.common.OAuth2AccessToken.REFRESH_TOKEN
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer


@Configuration
@EnableAuthorizationServer
class AuthorizationServerConfig(@Autowired @Qualifier("authenticationManagerBean")
                                val authenticationManager: AuthenticationManager) : AuthorizationServerConfigurerAdapter() {

    @Value("\${security.jwt.token.secret-key:secret-key}")
    private val secretKey: String? = null
    val CLIEN_ID = "devglan-client"
    val CLIENT_SECRET = "$2a$04\$e/c1/RfsWuThaWFCrcCuJeoyvwCV0URN/6Pn9ZFlrtIWaU/vj/BfG"
    val GRANT_TYPE_PASSWORD = "password"
    val AUTHORIZATION_CODE = "authorization_code"
    val REFRESH_TOKEN = "refresh_token"
    val IMPLICIT = "implicit"
    val SCOPE_READ = "read"
    val SCOPE_WRITE = "write"
    val TRUST = "trust"
    val ACCESS_TOKEN_VALIDITY_SECONDS = 1 * 60 * 60
    val FREFRESH_TOKEN_VALIDITY_SECONDS = 6 * 60 * 60

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints.tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter())
                .authenticationManager(authenticationManager)
    }

    @Throws(Exception::class)
    override fun configure(configurer: ClientDetailsServiceConfigurer) {
        configurer.inMemory()
                .withClient(CLIEN_ID)
                .secret(CLIENT_SECRET)
                .authorizedGrantTypes(GRANT_TYPE_PASSWORD, AUTHORIZATION_CODE, REFRESH_TOKEN, IMPLICIT)
                .scopes(SCOPE_READ, SCOPE_WRITE, TRUST)
                .accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS).refreshTokenValiditySeconds(FREFRESH_TOKEN_VALIDITY_SECONDS)
    }

    @Bean
    fun tokenStore(): TokenStore {
        return JwtTokenStore(accessTokenConverter())
    }

    @Bean
    fun accessTokenConverter(): JwtAccessTokenConverter {
        val converter = JwtAccessTokenConverter()
        converter.setSigningKey(secretKey)
        return converter
    }

    @Bean
    @Primary
    fun tokenServices(): DefaultTokenServices {
        val defaultTokenServices = DefaultTokenServices()
        defaultTokenServices.setTokenStore(tokenStore())
        defaultTokenServices.setSupportRefreshToken(true)
        return defaultTokenServices
    }
}