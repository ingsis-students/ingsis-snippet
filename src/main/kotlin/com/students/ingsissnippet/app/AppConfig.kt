package com.students.ingsissnippet.app

import com.students.ingsissnippet.server.CorrelationIdInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class AppConfig(private val correlationIdInterceptor: CorrelationIdInterceptor) {

    @Bean
    fun restTemplate(): RestTemplate {
        val restTemplate = RestTemplate()
        restTemplate.interceptors.add(correlationIdInterceptor) // Adding the interceptor
        return restTemplate
    }
}
