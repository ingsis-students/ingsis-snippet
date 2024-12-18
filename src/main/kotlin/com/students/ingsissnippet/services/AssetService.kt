package com.students.ingsissnippet.services

import com.students.ingsissnippet.constants.ASSETSERVICE_URL
import com.students.ingsissnippet.routes.AssetServiceRoutes
import org.springframework.aot.generate.Generated
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
@Generated
class AssetService(private val restTemplate: RestTemplate) : AssetServiceRoutes {
    override fun get(directory: String, id: Long): String {
        val response = restTemplate.getForObject(
            "$ASSETSERVICE_URL/$directory/$id",
            String::class.java
        )
        return response ?: throw Exception("Snippet not found")
    }

    override fun put(directory: String, id: Long, content: String): String {
        restTemplate.put(
            "$ASSETSERVICE_URL/$directory/$id",
            content,
            String::class.java
        )
        return "Snippet updated"
    }

    override fun delete(directory: String, id: Long) {
        restTemplate.delete("$ASSETSERVICE_URL/$directory/$id")
    }

    override fun exists(directory: String, id: Long): Boolean {
        return try {
            restTemplate.getForObject(
                "$ASSETSERVICE_URL/$directory/$id",
                String::class.java
            )
            true
        } catch (e: Exception) {
            false
        }
    }
}
