package com.students.ingsissnippet.server

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.io.IOException

@Component
@Order(2)
class RequestLogFilter : Filter {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        chain: FilterChain
    ) {
        if (request is HttpServletRequest && response is HttpServletResponse) {
            val httpRequest = request

            val uri = httpRequest.requestURI
            val method = httpRequest.method
            val prefix = "$method $uri"

            try {
                chain.doFilter(request, response)
            } finally {
                val statusCode = response.status
                logger.info("{} - {}", prefix, statusCode)
            }
        } else {
            chain.doFilter(request, response)
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RequestLogFilter::class.java)
    }
}
