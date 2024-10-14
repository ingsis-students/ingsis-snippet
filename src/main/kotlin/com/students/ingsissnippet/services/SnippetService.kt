package com.students.ingsissnippet.services

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.students.ingsissnippet.entities.dto.FormatDTO
import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.entities.dto.DTO
import com.students.ingsissnippet.entities.dto.InterpretDTO
import com.students.ingsissnippet.entities.dto.LinterDTO
import com.students.ingsissnippet.repositories.SnippetRepository
import com.students.ingsissnippet.routes.SnippetServiceRoutes
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class SnippetService(
    private val snippetRepository: SnippetRepository,
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
) : SnippetServiceRoutes {

    override fun createSnippet(name: String, content: String, language: String, owner: String): Snippet {
        val snippet = Snippet(name = name, content = content, language = language, owner = owner)
        snippetRepository.save(snippet)
        addSnippetToUser(owner, snippet.id, "Owner")
        return snippet
    }

    override fun getSnippetOfId(id: Long): Snippet {
        return snippetRepository.findById(id).orElseThrow { NoSuchElementException("Snippet not found") }
    }

    override fun editSnippet(id: Long, content: String): Snippet? {
        checkIfExists(id, "edit")
        val snippet = snippetRepository.findById(id).get()
        val updatedSnippet = snippet.copy(content = content)
        snippetRepository.save(updatedSnippet)
        return updatedSnippet
    }

    override fun deleteSnippet(id: Long) {
        checkIfExists(id, "delete")
        snippetRepository.deleteById(id)
    }

    override fun executeSnippet(id: Long): String {
        checkIfExists(id, "interpret")

        val snippet = getSnippetOfId(id)
        val interpretDTO = InterpretDTO(
            version = "1.0",
            code = snippet.content
        )
        val entity = createHTTPEntity(interpretDTO)
        return executePostForParseService(entity, "/interpret")
    }

    // FIXME Como todavía no sabemos como nos van a mandar las rules lo dejo así
    override fun analyzeSnippet(id: Long): String {
        checkIfExists(id, "analyze")

        val snippet = getSnippetOfId(id)

        val rulesJson = getDefaultRule()

        val linterDTO = LinterDTO(
            version = "1.0",
            code = snippet.content,
            rules = rulesJson
        )

        val entity = createHTTPEntity(linterDTO)
        return executePostForParseService(entity, "/analyze")
    }

    override fun formatSnippet(id: Long): String {
        val snippet = getSnippetOfId(id)

        // FIXME Esto recibiría version y rules
        val formatDto = FormatDTO(
            version = "1.0",
            code = snippet.content,
            rules = objectMapper.readTree(
                """
                {
                    "space_around_equals": true,
                    "space_before_colon": true,
                    "space_after_colon": true
                }
                """.trimIndent()
            )
        )

        val entity = createHTTPEntity(formatDto)

        val formattedCode = executePostForParseService(entity, "/format")

        return formattedCode
    }

    override fun shareSnippet(snippetId: Long, fromEmail: String, toEmail: String): ResponseEntity<String> {
        checkIfExists(snippetId, "share")
        if (!checkIfOwner(snippetId, fromEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not the owner of the snippet")
        }
        addSnippetToUser(toEmail, snippetId, "Guest")
        return ResponseEntity.ok("Snippet shared with $toEmail")
    }

    override fun checkIfOwner(snippetId: Long, email: String): Boolean {
        val body: Map<String, Any> = mapOf("snippetId" to snippetId, "email" to email)
        val entity = HttpEntity(body, getJsonHeaders())
        val response = executePostForPermissionService(entity, "/check-owner")
        return response == "User is the owner of the snippet"
    }

    override fun validateSnippet(id: Long): String {
        // TODO Call ValidatorService to validate the snippet, missing impl on parse service
        val body: Map<String, Any> = emptyMap()
        val entity = HttpEntity(body, getJsonHeaders())
        val response = executePostForPermissionService(entity, "/validate")
        return response.toString()
    }

    override fun addSnippetToUser(email: String, id: Long, role: String) {
        checkIfExists(id, "add")
        val body: Map<String, Any> = mapOf("snippetId" to id, "role" to role)
        val entity = HttpEntity(body, getJsonHeaders())
        executePostForPermissionService(entity, "/add-snippet/$email")
    }

    // ~ PRIVATE FUNCTIONS ~ //

    private fun executePostForParseService(entity: HttpEntity<DTO>, route: String): String {
        return restTemplate.postForObject(
            "http://localhost:8081$route",
            entity,
            String::class.java
        ).toString()
    }

    fun executePostForPermissionService(entity: HttpEntity<Map<String, Any>>, string: String): String? {
        return restTemplate.postForObject(
            "http://localhost:8082/api/user$string",
            entity,
            String::class.java
        )
    }

    private fun createHTTPEntity(dto: DTO): HttpEntity<DTO> {
        return HttpEntity(dto, getJsonHeaders())
    }

    private fun getJsonHeaders(): MultiValueMap<String, String>? {
        return HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
    }

    fun checkIfExists(id: Long, operation: String) {
        if (!snippetRepository.existsById(id)) {
            throw NoSuchElementException("Snippet not found when trying to $operation it")
        }
    }

    // TODO Esto es TEMPORAL, eventualmente vuela, es para probar HTTP requests del linter
    private fun getDefaultRule(): Map<String, JsonNode> {
        val jsonNode = objectMapper.readTree(
            """
            {
                "NamingFormatCheck": {
                    "namingPatternName": "camelCase"
                }
            }
            """.trimIndent()
        )

        val rules: Map<String, JsonNode> = objectMapper.convertValue(
            jsonNode,
            objectMapper.typeFactory.constructMapType(Map::class.java, String::class.java, JsonNode::class.java)
        )
        return rules
    }
}
