package com.students.ingsissnippet.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.students.ingsissnippet.constants.PARSE_URL
import com.students.ingsissnippet.dtos.request_dtos.DTO
import com.students.ingsissnippet.dtos.request_dtos.FormatDTO
import com.students.ingsissnippet.dtos.request_dtos.InterpretDTO
import com.students.ingsissnippet.dtos.request_dtos.LinterDTO
import com.students.ingsissnippet.dtos.request_dtos.ValidateDTO
import com.students.ingsissnippet.dtos.response_dtos.FullSnippet
import com.students.ingsissnippet.dtos.request_dtos.TestParseDTO
import com.students.ingsissnippet.routes.ParseServiceRoutes
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class ParseService(
    @Lazy private val snippetService: SnippetService,
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
) : ParseServiceRoutes {

    override fun execute(id: Long): String {
        snippetService.checkIfExists(id, "interpret")

        val snippet = snippetService.get(id)
        val interpretDTO = InterpretDTO(
            version = snippet.version,
            code = snippet.content
        )
        val entity = createHTTPEntity(interpretDTO)
        return executePost(entity, "interpret")
    }

    // FIXME Como todavía no sabemos como nos van a mandar las rules lo dejo así
    override fun analyze(id: Long): String {
        snippetService.checkIfExists(id, "analyze")
        val snippet = snippetService.get(id)
        val rulesJson = getDefaultRule()
        val linterDTO = LinterDTO(
            version = snippet.version,
            code = snippet.content,
            rules = rulesJson
        )
        val entity = createHTTPEntity(linterDTO)
        return executePost(entity, "analyze")
    }

    override fun format(id: Long): FullSnippet {
        val snippet = snippetService.get(id)

        // FIXME Esto recibiría version y rules
        val formatDto = FormatDTO(
            version = snippet.version,
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
        val formattedCode = executePost(entity, "format")
        return snippetService.update(id, formattedCode)
    }

    override fun validate(token: String, id: Long): List<String> {
        val snippet = snippetService.get(id)

        val body: DTO = ValidateDTO(
            version = snippet.version,
            code = snippet.content
        )

        val entity = HttpEntity(body, getJsonAuthorizedHeaders(token))

        val response = restTemplate.postForObject(
            "$PARSE_URL/validate",
            entity,
            List::class.java
        )

        return objectMapper.convertValue(
            response,
            object : TypeReference<List<String>>() {}
        )
    }

    override fun test(
        token: String,
        snippetId: Long,
        inputs: List<String>,
        outputs: List<String>
    ): ResponseEntity<String> {
        val snippet = snippetService.get(snippetId)
        val testDTO = TestParseDTO(
            version = snippet.version,
            snippetId = snippet.id,
            inputs = inputs,
            outputs = outputs
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", token)
        }

        val entity = HttpEntity(testDTO, headers)

        val response = restTemplate.exchange(
            "$PARSE_URL/test",
            HttpMethod.POST,
            entity,
            String::class.java,
        )

        return response
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
            "$PARSE_URL/$route",
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

    private fun getJsonAuthorizedHeaders(token: String): MultiValueMap<String, String> {
        return HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", token)
        }
    }
}
