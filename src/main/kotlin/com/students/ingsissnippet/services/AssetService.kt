package com.students.ingsissnippet.services

import com.students.ingsissnippet.routes.AssetServiceRoutes
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class AssetService(private val restTemplate: RestTemplate) : AssetServiceRoutes {
    override fun get(directory: String, id: Long): String {
        val response = restTemplate.getForObject(
            "http://localhost:8084/v1/asset/$directory/$id",
            String::class.java
        )
        return response ?: "Snippet not found"
    }

    override fun put(directory: String, id: Long, content: String): String {
        restTemplate.put(
            "http://localhost:8084/v1/asset/$directory/$id",
            content,
            String::class.java
        )
        return "Snippet updated"
    }

    override fun delete(id: Long) {
        restTemplate.delete("http://localhost:8084/v1/asset/snippets/$id")
    }
}
