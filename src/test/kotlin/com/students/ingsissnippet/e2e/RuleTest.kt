package com.students.ingsissnippet.e2e

import com.fasterxml.jackson.databind.ObjectMapper
import com.students.ingsissnippet.config.producers.RedisFormatRuleProducer
import com.students.ingsissnippet.config.producers.RedisLinterRuleProducer
import com.students.ingsissnippet.dtos.request_types.Rule
import com.students.ingsissnippet.dtos.response_dtos.FullSnippet
import com.students.ingsissnippet.services.AssetService
import com.students.ingsissnippet.services.PermissionService
import com.students.ingsissnippet.services.SnippetService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content

import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class RuleTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var permissionService: PermissionService

    @MockBean
    private lateinit var assetService: AssetService

    @MockBean
    private lateinit var snippetService: SnippetService

    @MockBean
    private lateinit var linterRuleProducer: RedisLinterRuleProducer

    @MockBean
    private lateinit var redisFormatRuleProducer: RedisFormatRuleProducer

    private val token = "Bearer token"
    private val rulesJson = """
        [
            { "id": "1", "name": "rule1", "isActive": true },
            { "id": "2", "name": "rule2", "isActive": false }
        ]
    """.trimIndent()

    @BeforeEach
    fun setup() {
        val mockResponseEntity: ResponseEntity<Long> = ResponseEntity.ok(1L)
        val snippetListMock: ResponseEntity<List<Long>> = ResponseEntity.ok(listOf(1L))
        val fullSnippet = FullSnippet()

        whenever(permissionService.validate(token)).thenReturn(mockResponseEntity)
        whenever(assetService.exists(anyString(), anyLong())).thenReturn(true)
        whenever(assetService.get(anyString(), anyLong())).thenReturn(rulesJson)
        whenever(assetService.put(anyString(), anyLong(), anyString())).thenReturn("String Updated")
        whenever(permissionService.getSnippetsId(anyString(), anyLong())).thenReturn(snippetListMock)
        whenever(snippetService.updateStatus(anyLong(), any())).thenReturn(fullSnippet)
        whenever(snippetService.get(anyLong())).thenReturn(fullSnippet)
    }

    @Test
    fun `should get lint rules successfully`() {
        mockMvc.perform(
            get("/api/snippets/lint/rules")
                .header("Authorization", token)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.size()").isNotEmpty)
    }

    @Test
    fun `should get format rules successfully`() {
        mockMvc.perform(
            get("/api/snippets/format/rules")
                .header("Authorization", token)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.size()").isNotEmpty)
    }

    @Test
    fun `should set default lint rules successfully`() {
        mockMvc.perform(
            get("/api/snippets/lint/rules/default")
                .header("Authorization", token)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(rulesJson))
    }

    @Test
    fun `should set default format rules successfully`() {
        mockMvc.perform(
            post("/api/snippets/format/rules/default")
                .header("Authorization", token)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(rulesJson))
    }

    @Test
    fun `should update lint rules successfully`() {
        val lintRules = listOf(
            Rule("1", "rule1", true),
            Rule("2", "rule2", false)
        )
        val lintRulesJson = objectMapper.writeValueAsString(lintRules)

        mockMvc.perform(
            post("/api/snippets/lint/rules")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(lintRulesJson)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should update format rules successfully`() {
        val formatRules = listOf(
            Rule("1", "rule1", true),
            Rule("2", "rule2", false)
        )
        val formatRulesJson = objectMapper.writeValueAsString(formatRules)

        mockMvc.perform(
            post("/api/snippets/format/rules")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(formatRulesJson)
        )
            .andExpect(status().isOk)
    }
}
