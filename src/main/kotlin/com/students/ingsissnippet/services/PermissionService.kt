package com.students.ingsissnippet.services

import com.students.ingsissnippet.routes.PermissionServiceRoutes
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class PermissionService(private val restTemplate: RestTemplate) : PermissionServiceRoutes {

    override fun addSnippetToUser(email: String, snippetId: Long, role: String) {
        val body: Map<String, Any> = mapOf("snippetId" to snippetId, "role" to role)
        val entity = HttpEntity(body, getJsonHeaders())
        executePost(entity, "/add-snippet/$email")
    }

    override fun checkIfOwner(snippetId: Long, email: String): Boolean {
        val body: Map<String, Any> = mapOf("snippetId" to snippetId, "email" to email)
        val entity = HttpEntity(body, getJsonHeaders())
        val response = executePost(entity, "/check-owner")
        return response == "User is the owner of the snippet"
    }

    override fun shareSnippet(snippetId: Long, fromEmail: String, toEmail: String): ResponseEntity<String> {
        if (!checkIfOwner(snippetId, fromEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not the owner of the snippet")
        }
        addSnippetToUser(toEmail, snippetId, "Guest")
        return ResponseEntity.ok("Snippet shared with $toEmail")
    }

    private fun getJsonHeaders(): MultiValueMap<String, String>? {
        return HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
    }

    override fun executePost(entity: HttpEntity<Map<String, Any>>, string: String): String? {
        return restTemplate.postForObject(
            "http://localhost:8082/api/user$string",
            entity,
            String::class.java
        )
    }
}
