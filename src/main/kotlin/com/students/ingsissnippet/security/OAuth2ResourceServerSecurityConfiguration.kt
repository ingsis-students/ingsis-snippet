package com.students.ingsissnippet.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpMethod.PUT
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class OAuth2ResourceServerSecurityConfiguration(
    @Value(
        "\${auth0.audience}"
    )
    val audience: String,
    @Value(
        "\${spring.security.oauth2.resourceserver.jwt.issuer-uri}"
    )
    val issuer: String
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests {
            it
                .requestMatchers("/").permitAll()
                .requestMatchers(GET, "/snippets/get/{id}").hasAuthority("SCOPE_read:snippets")
                .requestMatchers(POST, "/snippets/create").hasAuthority("SCOPE_write:snippets")
                .requestMatchers(POST, "/snippets/edit/{id}").hasAuthority("SCOPE_write:snippets")
                .requestMatchers(POST, "/snippets/delete/{id}").hasAuthority("SCOPE_write:snippets")
                .requestMatchers(POST, "/snippets/format/{id}").hasAuthority("SCOPE_write:snippets")
                .requestMatchers(POST, "/snippets/execute/{id}").hasAuthority("SCOPE_write:snippets")
                .requestMatchers(POST, "/snippets/validate/{id}").hasAuthority("SCOPE_write:snippets")
                .requestMatchers(POST, "/snippets/lint/{id}").hasAuthority("SCOPE_write:snippets")
                .requestMatchers(POST, "/snippets/share/{id}").hasAuthority("SCOPE_write:snippets")
                .anyRequest().authenticated()
        }
            .oauth2ResourceServer { it.jwt(withDefaults()) }
            .cors { it.disable() }
            .csrf { it.disable() }
        return http.build()
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val jwtDecoder = NimbusJwtDecoder.withIssuerLocation(issuer).build()
        val audienceValidator: OAuth2TokenValidator<Jwt> = AudienceValidator(audience)
        val withIssuer: OAuth2TokenValidator<Jwt> = JwtValidators.createDefaultWithIssuer(issuer)
        val withAudience: OAuth2TokenValidator<Jwt> = DelegatingOAuth2TokenValidator(withIssuer, audienceValidator)
        jwtDecoder.setJwtValidator(withAudience)
        return jwtDecoder
    }
}
