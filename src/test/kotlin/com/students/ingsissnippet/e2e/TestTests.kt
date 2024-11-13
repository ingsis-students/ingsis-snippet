package com.students.ingsissnippet.e2e

import com.fasterxml.jackson.databind.ObjectMapper
import com.students.ingsissnippet.config.producers.RedisFormatRuleProducer
import com.students.ingsissnippet.config.producers.RedisLinterRuleProducer
import com.students.ingsissnippet.dtos.request_dtos.CreateTestDTO
import com.students.ingsissnippet.dtos.request_types.Compliance
import com.students.ingsissnippet.entities.Language
import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.entities.Test as SnippetTest
import com.students.ingsissnippet.repositories.LanguageRepository
import com.students.ingsissnippet.repositories.SnippetRepository
import com.students.ingsissnippet.repositories.TestRepository
import com.students.ingsissnippet.services.ParseService
import com.students.ingsissnippet.services.TestService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyList
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class TestTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var testService: TestService

    @Autowired
    private lateinit var testRepository: TestRepository

    @Autowired
    private lateinit var snippetRepository: SnippetRepository

    @Autowired
    private lateinit var languageRepository: LanguageRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var parseService: ParseService

    @MockBean
    private lateinit var redisLinterRuleProducer: RedisLinterRuleProducer

    @MockBean
    private lateinit var redisFormatRuleProducer: RedisFormatRuleProducer

    @BeforeEach
    fun setup() {
        testRepository.deleteAll()
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
        val testEntity = SnippetTest(
            name = "Test Name",
            snippet = snippet
        )
        testRepository.save(testEntity)
        whenever(parseService.test(anyString(), anyLong(), anyList(), anyList()))
            .thenReturn(listOf())
    }

    @Test
    @WithMockUser
    fun `should retrieve all tests for a snippet`() {
        val snippet = snippetRepository.findAll()[0]

        mockMvc.perform(get("/api/tests/snippet/${snippet.id}"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value("Test Name"))
    }

    @Test
    @WithMockUser
    fun `should add a new test to a snippet`() {
        val snippet = snippetRepository.findAll()[0]

        val createTestDTO = CreateTestDTO(
            name = "New Test",
            input = listOf("input1", "input2"),
            output = listOf("output1")
        )

        mockMvc.perform(
            post("/api/tests/snippet/${snippet.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTestDTO))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("New Test"))
            .andExpect(jsonPath("$.input.length()").value(2))
            .andExpect(jsonPath("$.output.length()").value(1))
    }

    @Test
    @WithMockUser
    fun `should delete a test by ID`() {
        val test = testRepository.findAll()[0]

        mockMvc.perform(delete("/api/tests/${test.id}"))
            .andExpect(status().isNoContent)

        assertThat(testRepository.existsById(test.id)).isFalse
    }

    @Test
    @WithMockUser
    fun `should run tests for a snippet and return success`() {
        val test = testRepository.findAll()[0]

        mockMvc.perform(
            post("/api/tests/{id}/run", test.id)
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("success"))
    }

    @Test
    @WithMockUser
    fun `should run all tests for a snippet and return pass-fail count`() {
        val snippet = snippetRepository.findAll()[0]

        testRepository.save(SnippetTest(name = "Test 2", snippet = snippet))

        mockMvc.perform(
            post("/api/tests/{snippetId}/run-all", snippet.id)
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
    }
}
