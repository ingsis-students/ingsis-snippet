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
import org.slf4j.MDC
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

                // Retrieve the correlation ID from MDC and log it
                val correlationId = MDC.get(CorrelationIdFilter.CORRELATION_ID_KEY) ?: "none"

                // Include correlation ID in the log
                logger.info("{} - {} [correlation-id={}] ", prefix, statusCode, correlationId)
            }
        } else {
            chain.doFilter(request, response)
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RequestLogFilter::class.java)
    }
}
