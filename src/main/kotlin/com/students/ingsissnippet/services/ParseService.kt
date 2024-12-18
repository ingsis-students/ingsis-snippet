package com.students.ingsissnippet.services

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.students.ingsissnippet.constants.PARSE_URL
import com.students.ingsissnippet.dtos.request_dtos.DTO
import com.students.ingsissnippet.dtos.request_dtos.FormatDTO
import com.students.ingsissnippet.dtos.request_dtos.InterpretDTO
import com.students.ingsissnippet.dtos.request_dtos.ValidateDTO
import com.students.ingsissnippet.dtos.request_dtos.TestParseDTO
import com.students.ingsissnippet.routes.ParseServiceRoutes
import org.springframework.context.annotation.Lazy
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
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

    override fun format(version: String, content: String, rules: String, token: String): String {
        val formatDto: DTO = FormatDTO(
            version = version,
            code = content,
            rules = ObjectMapper().readTree(rules),
        )
        println("reglas pasadas como JsonNode:$rules")
        val entity = HttpEntity(formatDto, getJsonAuthorizedHeaders(token))
        return executePost(entity, "format")
    }

    override fun validate(token: String, version: String, content: String): List<String> {
        val body: DTO = ValidateDTO(
            version = version,
            code = content
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
    ): List<String> {
        val snippet = snippetService.get(snippetId)
        val testDTO = TestParseDTO(
            version = snippet.version,
            snippetId = snippet.id,
            inputs = inputs,
            outputs = outputs
        )

        val headers = getJsonAuthorizedHeaders(token)
        val entity = HttpEntity(testDTO, headers)

        val response = restTemplate.exchange(
            "$PARSE_URL/test",
            HttpMethod.POST,
            entity,
            object : ParameterizedTypeReference<List<String>>() {}
        )

        return response.body ?: emptyList()
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
