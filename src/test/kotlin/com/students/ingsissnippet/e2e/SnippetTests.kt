package com.students.ingsissnippet.e2e

import com.fasterxml.jackson.databind.ObjectMapper
import com.students.ingsissnippet.config.producers.RedisFormatRuleProducer
import com.students.ingsissnippet.config.producers.RedisLinterRuleProducer
import com.students.ingsissnippet.dtos.request_types.Compliance
import com.students.ingsissnippet.dtos.request_types.ContentRequest
import com.students.ingsissnippet.dtos.request_types.ShareRequest
import com.students.ingsissnippet.dtos.request_types.SnippetRequest
import com.students.ingsissnippet.dtos.response_dtos.FullSnippet
import com.students.ingsissnippet.dtos.response_dtos.SnippetUserDto
import com.students.ingsissnippet.entities.Language
import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.repositories.LanguageRepository
import com.students.ingsissnippet.repositories.SnippetRepository
import com.students.ingsissnippet.services.AssetService
import com.students.ingsissnippet.services.LanguageService
import com.students.ingsissnippet.services.ParseService
import com.students.ingsissnippet.services.PermissionService
import com.students.ingsissnippet.services.SnippetService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.anyLong
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class SnippetTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var snippetService: SnippetService

    @Autowired
    private lateinit var snippetRepository: SnippetRepository

    @Autowired
    private lateinit var languageRepository: LanguageRepository

    @MockBean
    private lateinit var permissionService: PermissionService

    @MockBean
    private lateinit var languageService: LanguageService

    @MockBean
    private lateinit var assetService: AssetService

    @MockBean
    private lateinit var parseService: ParseService

    @MockBean
    private lateinit var redisLinterRuleProducer: RedisLinterRuleProducer

    @MockBean
    private lateinit var redisFormatRuleProducer: RedisFormatRuleProducer

    private var token = "Bearer token"

    @BeforeEach
    fun setup() {
        snippetRepository.deleteAll()
        languageRepository.deleteAll()

        val language = Language(
            name = "printscript",
            version = "1.0",
            extension = "prs"
        )
        languageRepository.save(language)

        val snippet = Snippet(
            name = "Test Snippet",
            owner = "Test Owner",
            status = Compliance.PENDING,
            language = language
        )
        snippetRepository.save(snippet)

        val snippetUserDto = SnippetUserDto(
            snippetId = snippet.id,
            role = "Owner"
        )

        whenever(permissionService.getSnippetsOfUser(anyString(), anyString())).thenReturn(listOf(snippetUserDto))
        whenever(assetService.exists(anyString(), anyLong())).thenReturn(true)
        whenever(assetService.put(anyString(), anyLong(), anyString())).thenReturn("String Updated")
        whenever(languageService.getLanguageById(anyLong())).thenReturn(language)
        whenever(parseService.validate(anyString(), anyString(), anyString())).thenReturn(emptyList())
    }

    @Test
    fun `should get snippet by user Id and filters`() {
        whenever(assetService.get(anyString(), anyLong())).thenReturn("[content]")

        mockMvc.perform(
            get("/api/snippets/user")
                .param("userId", "1")
                .header("Authorization", token)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.count").value(1))
    }

    @Test
    fun `get snippet by id`() {
        whenever(assetService.get(anyString(), anyLong())).thenReturn("[content]")
        val snippet = snippetRepository.findAll()[0]

        mockMvc.perform(
            get("/api/snippets/{id}", snippet.id)
                .header("Authorization", token)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value(snippet.name))
    }

    @Test
    fun `should create a new snippet`() {
        val language = languageRepository.findAll()[0]
        val snippetRequest = SnippetRequest(
            name = "New Snippet",
            content = "[content]",
            languageId = language.id.toString(),
            owner = "Test Owner"
        )

        mockMvc.perform(
            post("/api/snippets/")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(snippetRequest))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value(snippetRequest.name))
            .andExpect(jsonPath("$.content").value(snippetRequest.content))
            .andExpect(jsonPath("$.errors").isEmpty)
    }

    @Test
    fun `should update an existing snippet`() {
        val snippet = snippetRepository.findAll()[0]
        val updatedContent = "[updated content]"
        val contentRequest = ContentRequest(content = updatedContent)

        whenever(parseService.validate(anyString(), anyString(), anyString())).thenReturn(emptyList())
        whenever(assetService.put("snippets", snippet.id, updatedContent)).thenReturn("Content Updated")

        mockMvc.perform(
            put("/api/snippets/{id}", snippet.id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(contentRequest))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value(snippet.name))
            .andExpect(jsonPath("$.content").value(updatedContent))
            .andExpect(jsonPath("$.errors").isEmpty)
    }

    @Test
    fun `should delete an existing snippet`() {
        val snippet = snippetRepository.findAll()[0]

        mockMvc.perform(
            post("/api/snippets/delete/{id}", snippet.id)
                .header("Authorization", token)
        )
            .andExpect(status().isNoContent)

        mockMvc.perform(
            get("/api/snippets/{id}", snippet.id)
                .header("Authorization", token)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should share a snippet successfully`() {
        val snippet = snippetRepository.findAll()[0]
        val toEmail = "to@example.com"
        val shareRequest = ShareRequest(snippet.name, toEmail)
        val fullSnippet = FullSnippet(snippet, "[content]")
        val mockResponse = ResponseEntity.ok(fullSnippet)

        whenever(permissionService.shareSnippet(token, snippet.id, snippet.owner, toEmail, fullSnippet))
            .thenReturn(mockResponse)
        whenever(assetService.get("snippets", snippet.id)).thenReturn("content")
        whenever(assetService.exists("lint-warnings", snippet.id)).thenReturn(false)

        mockMvc.perform(
            post("/api/snippets/share/{id}", snippet.id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(objectMapper.writeValueAsString(shareRequest))
        )
            .andExpect(status().isOk)
    }
}
