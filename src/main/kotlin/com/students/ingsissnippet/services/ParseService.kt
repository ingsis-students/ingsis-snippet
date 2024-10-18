package com.students.ingsissnippet.services

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.students.ingsissnippet.entities.request_dtos.DTO
import com.students.ingsissnippet.entities.request_dtos.FormatDTO
import com.students.ingsissnippet.entities.request_dtos.InterpretDTO
import com.students.ingsissnippet.entities.request_dtos.LinterDTO
import com.students.ingsissnippet.entities.request_dtos.ValidateDTO
import com.students.ingsissnippet.routes.ParseServiceRoutes
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class ParseService(
    private val snippetService: SnippetService,
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
) : ParseServiceRoutes {

    override fun executeSnippet(id: Long): String {
        snippetService.checkIfExists(id, "interpret")

        val snippet = snippetService.getSnippetOfId(id)
        val interpretDTO = InterpretDTO(
            version = "1.0",
            code = snippet.content
        )
        val entity = createHTTPEntity(interpretDTO)
        return executePost(entity, "/interpret")
    }

    // FIXME Como todavía no sabemos como nos van a mandar las rules lo dejo así
    override fun analyzeSnippet(id: Long): String {
        snippetService.checkIfExists(id, "analyze")

        val snippet = snippetService.getSnippetOfId(id)

        val rulesJson = getDefaultRule()

        val linterDTO = LinterDTO(
            version = "1.0",
            code = snippet.content,
            rules = rulesJson
        )

        val entity = createHTTPEntity(linterDTO)
        return executePost(entity, "/analyze")
    }

    override fun formatSnippet(id: Long): String {
        val snippet = snippetService.getSnippetOfId(id)

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

        val formattedCode = executePost(entity, "/format")

        return formattedCode
    }

    override fun validateSnippet(id: Long): String {
        // TODO Call ValidatorService to validate the snippet, missing impl on parse service
        val body: DTO = ValidateDTO("", "")
        val entity = HttpEntity(body, getJsonHeaders())
        val response = executePost(entity, "/validate")
        return response.toString()
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

    override fun executePost(entity: HttpEntity<DTO>, route: String): String {
        return restTemplate.postForObject(
            "http://localhost:8081/api/printscript$route",
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
}
