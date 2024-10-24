package com.students.ingsissnippet.services

import com.students.ingsissnippet.routes.AssetServiceRoutes
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class AssetService(private val restTemplate: RestTemplate) : AssetServiceRoutes {
    override fun get(id: Long): String {
        val response = restTemplate.getForObject(
            "http://localhost:8084/v1/asset/snippets/$id",
            String::class.java
        )
        return response ?: "Snippet not found"
    }

    override fun put(id: Long, content: String): String {
        val response = restTemplate.postForObject(
            "http://localhost:8084/v1/asset/snippets/$id",
            content,
            String::class.java
        )
        return response ?: "Failed to update snippet"
    }

    override fun delete(id: Long) {
        restTemplate.delete("http://localhost:8084/v1/asset/snippets/$id")
    }
}
