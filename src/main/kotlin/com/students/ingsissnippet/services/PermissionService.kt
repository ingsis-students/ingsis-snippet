package com.students.ingsissnippet.services

import com.students.ingsissnippet.constants.PERMISSION_URL
import com.students.ingsissnippet.dtos.response_dtos.FullSnippet
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
class PermissionService(
    private val restTemplate: RestTemplate,
) : PermissionServiceRoutes {

    override fun addSnippetToUser(token: String, email: String, snippetId: Long, role: String) {
        val body: Map<String, Any> = mapOf("snippetId" to snippetId, "role" to role)

        val headers = getJsonHeaders().apply {
            set("Authorization", token)
        }

        val entity = HttpEntity(body, headers)
        executePost(entity, "/add-snippet/$email")
    }

    override fun checkIfOwner(snippetId: Long, email: String): Boolean {
        val body: Map<String, Any> = mapOf("snippetId" to snippetId, "email" to email)
        val entity = HttpEntity(body, getJsonHeaders())
        val response = executePost(entity, "/check-owner")
        return response == "User is the owner of the snippet"
    }

    override fun shareSnippet(
        token: String,
        snippetId: Long,
        fromEmail: String,
        toEmail: String,
        snippet: FullSnippet
    ): ResponseEntity<FullSnippet> {
        if (fromEmail == toEmail) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header("Share-Status", "You can't share a snippet with yourself")
                .body(FullSnippet())
        }

//        if (!checkIfOwner(sgit config core.hooksPath ./git-hooksnippetId, fromEmail)) {
//            return ResponseEntity
//                .status(HttpStatus.FORBIDDEN)
//                .header("Share-Status", "You are not the owner of the snippet")
//                .body(FullSnippet())
//        }

        addSnippetToUser(token, toEmail, snippetId, "Guest")
        return ResponseEntity
            .status(HttpStatus.OK)
            .header("Share-Status", "Snippet shared with $toEmail")
            .body(snippet)
    }

    private fun getJsonHeaders(): MultiValueMap<String, String> {
        return HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
    }

    override fun executePost(entity: HttpEntity<Map<String, Any>>, string: String): String? {
        return restTemplate.postForObject(
            "$PERMISSION_URL$string",
            entity,
            String::class.java
        )
    }

    override fun validate(jwt: String): ResponseEntity<Long> {
        return try {
            val headers = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
                set("Authorization", "Bearer $jwt")
            }
            val entity = HttpEntity<Void>(headers)

            restTemplate.exchange(
                "$PERMISSION_URL/validate",
                HttpMethod.GET,
                entity,
                Long::class.java
            )
        } catch (e: Exception) {
            print("VALIDATE -> Error validating token: $e")
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    override fun getSnippets(id: Long): ResponseEntity<List<Snippet>> {
        val body: Map<String, Any> = mapOf("id" to id)
        val entity = HttpEntity(body, getJsonHeaders())

        val responseType = object : ParameterizedTypeReference<List<Snippet>>() {}

        val response = restTemplate.exchange( // exchange deja recibir listas de objetos.
            "$PERMISSION_URL/snippets",
            HttpMethod.GET,
            entity,
            responseType,
        )
        return ResponseEntity.ok(response.body)
    }
}
