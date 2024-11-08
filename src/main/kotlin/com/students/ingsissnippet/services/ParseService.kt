package com.students.ingsissnippet.services

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.students.ingsissnippet.dtos.request_dtos.DTO
import com.students.ingsissnippet.dtos.request_dtos.FormatDTO
import com.students.ingsissnippet.dtos.request_dtos.InterpretDTO
import com.students.ingsissnippet.dtos.request_dtos.LinterDTO
import com.students.ingsissnippet.dtos.request_dtos.ValidateDTO
import com.students.ingsissnippet.dtos.response_dtos.FullSnippet
import com.students.ingsissnippet.dtos.request_dtos.TestParseDTO
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
    private val assetService: AssetService,
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
        return executePost(entity, "/interpret")
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
        return executePost(entity, "/analyze")
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
        val formattedCode = executePost(entity, "/format")
        return snippetService.update(id, formattedCode)
    }

    override fun validate(id: Long): String {
        val snippet = snippetService.get(id)
        val body: DTO = ValidateDTO(
            version = snippet.version,
            code = snippet.content
        )
        val entity = HttpEntity(body, getJsonHeaders())
        return executePost(entity, "/validate")
    }

    override fun test(snippetId: Long, inputs: List<String>, outputs: List<String>): List<String> {
        val snippet = snippetService.get(snippetId)
        val testDTO = TestParseDTO(
            version = snippet.version,
            code = snippet.content,
            inputs = inputs,
            outputs = outputs
        )
        val entity = createHTTPEntity(testDTO)

        return restTemplate.postForObject(
            "http://printscript-api:8080/api/printscript/test",
            entity,
            List::class.java
        ) as List<String>? ?: listOf("Error: No response from test service")
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
            "http://printscript-api:8080$route",
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
