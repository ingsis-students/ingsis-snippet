package com.students.ingsissnippet.services

import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.routes.PermissionServiceRoutes
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
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

    private fun getJsonHeaders(): MultiValueMap<String, String> {
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

    override fun validate(jwt: String): ResponseEntity<Long> {
        val body: Map<String, Any> = mapOf("jwt" to jwt)
        val entity = HttpEntity(body, getJsonHeaders())
        return restTemplate.postForEntity(
            "http://localhost:8082/api/user/validate",
            entity,
            Long::class.java
        )
    }

    override fun getSnippets(id: Long): ResponseEntity<List<Snippet>> {
        val body: Map<String, Any> = mapOf("id" to id)
        val entity = HttpEntity(body, getJsonHeaders())

        val responseType = object : ParameterizedTypeReference<List<Snippet>>() {}

        val response = restTemplate.exchange( // exchange deja recibir listas de objetos.
            "http://localhost:8082/api/user/snippets",
            HttpMethod.GET,
            entity,
            responseType,
        )
        return ResponseEntity.ok(response.body)
    }
}
