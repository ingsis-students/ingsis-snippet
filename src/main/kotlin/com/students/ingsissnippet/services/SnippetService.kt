package com.students.ingsissnippet.services


import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.students.ingsissnippet.entities.dto.FormatDTO
import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.entities.dto.DTO
import com.students.ingsissnippet.entities.dto.InterpretDTO
import com.students.ingsissnippet.entities.dto.LinterDTO
import com.students.ingsissnippet.repositories.SnippetRepository
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class SnippetService(
    private val snippetRepository: SnippetRepository,
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
) {

    fun createSnippet(name: String, content: String, language: String): Snippet {
        val snippet =
            Snippet(name = name, content = content, language = language, owner = "admin", guests = emptyList())
        snippetRepository.save(snippet)
        return snippet
    }

    fun getSnippetOfId(id: Long): Snippet {
        return snippetRepository.findById(id).orElseThrow { NoSuchElementException("Snippet not found") }
    }

    fun editSnippet(id: Long, content: String): Snippet? {
        val snippetOptional = snippetRepository.findById(id)
        checkIfExists(id, "delete")

        val snippet = snippetOptional.get()
        val updatedSnippet = snippet.copy(content = content)
        snippetRepository.save(updatedSnippet)
        return updatedSnippet
    }

    fun deleteSnippet(id: Long) {
        checkIfExists(id, "delete")
        snippetRepository.deleteById(id)
    }

    fun executeSnippet(id: Long): String {
        checkIfExists(id, "interpret")

        val snippet = getSnippetOfId(id)
        val interpretDTO = InterpretDTO(
            version = "1.0",
            code = snippet.content
        )
        val entity = createHTTPEntity(interpretDTO)
        return executePostFor(entity, "/interpret")
    }

    // FIXME Como todavía no sabemos como nos van a mandar las rules lo dejo así
    fun analyzeSnippet(id: Long): String {
        checkIfExists(id, "analyze")

        val snippet = getSnippetOfId(id)

        val rulesJson = getDefaultRule()

        val linterDTO = LinterDTO(
            version = "1.0",
            code = snippet.content,
            rules = rulesJson
        )

        val entity = createHTTPEntity(linterDTO)
        return executePostFor(entity, "/analyze")
    }

    fun formatSnippet(id: Long): String {
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

        val formattedCode = executePostFor(entity, "/format")

        return formattedCode
    }


    fun shareSnippet(id: Long, string: String): Snippet {
        // TODO Call permissionService to share the snippet (Add to guests list)
        return getSnippetOfId(id)
    }

    fun validateSnippet(id: Long): Snippet {
        // TODO Call ValidatorService to validate the snippet
        return getSnippetOfId(id)
    }

    // ~ PRIVATE FUNCTIONS ~ //

    private fun executePostFor(entity: HttpEntity<DTO>, route: String): String {
        return restTemplate.postForObject(
            "http://localhost:8081$route",
            entity,
            String::class.java
        ).toString()
    }

    private fun createHTTPEntity(dto: DTO): HttpEntity<DTO> {
        return HttpEntity(dto, getJsonHeaders())
    }

    private fun getJsonHeaders(): MultiValueMap<String, String>? {
        return HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }
    }

    private fun checkIfExists(id: Long, operation: String) {
        if (!snippetRepository.existsById(id)) {
            throw NoSuchElementException("Snippet not found when trying to $operation it")
        }
    }

    // TODO Esto es TEMPORAL, eventualmente vuela, es para probar HTTP requests
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
            jsonNode, objectMapper.typeFactory.constructMapType(Map::class.java, String::class.java, JsonNode::class.java)
        )
        return rules
    }
}

