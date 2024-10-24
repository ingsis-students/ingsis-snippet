package com.students.ingsissnippet.tests

import com.students.ingsissnippet.config.producers.RedisLinterRuleProducer
import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.dtos.request_dtos.DTO
import com.students.ingsissnippet.entities.Language
import com.students.ingsissnippet.repositories.SnippetRepository
import com.students.ingsissnippet.services.ParseService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestTemplate
import java.util.Optional
import kotlin.test.Test

@SpringBootTest
@ActiveProfiles("test")
class ParseServiceTest {

    @Autowired
    lateinit var parseService: ParseService

    @MockBean
    lateinit var restTemplate: RestTemplate

    @MockBean
    lateinit var snippetRepository: SnippetRepository

    @MockBean
    private lateinit var redisLinterRuleProducer: RedisLinterRuleProducer

    @BeforeEach
    fun setup() {
        val language = Language(
            id = 1,
            name = "printscript",
            version = "1.0",
        )
        val snippet = Snippet(
            id = 1,
            name = "My Snippet",
            owner = "admin",
            language = language,
        )
        whenever(snippetRepository.existsById(any())).thenReturn(true)
        whenever(snippetRepository.findById(any())).thenReturn(Optional.of(snippet))
        whenever(snippetRepository.save(snippet)).thenReturn(snippet)
        whenever(
            restTemplate.postForObject(
                argThat { url: String? -> url?.contains("format") == true },
                any<HttpEntity<DTO>>(),
                eq(String::class.java)
            )
        ).thenAnswer {
            "Formatted snippet successfully"
        }

        whenever(
            restTemplate.postForObject(
                argThat { url: String? -> url?.contains("analyze") == true },
                any<HttpEntity<DTO>>(),
                eq(String::class.java)
            )
        ).thenAnswer {
            "Analyzed snippet successfully"
        }

        whenever(
            restTemplate.postForObject(
                argThat { url: String? -> url?.contains("validate") == true },
                any<HttpEntity<DTO>>(),
                eq(String::class.java)
            )
        ).thenAnswer {
            "Validate snippet successfully"
        }

        whenever(
            restTemplate.postForObject(
                argThat { url: String? -> url?.contains("interpret") == true },
                any<HttpEntity<DTO>>(),
                eq(String::class.java)
            )
        ).thenAnswer {
            "Executed snippet successfully"
        }
    }

    @AfterEach
    fun tearDown() {
        snippetRepository.deleteAll()
    }

    @Test
    fun `can format snippet`() {
        val snippet = parseService.format(1L)
        assert(snippet.content == "Formatted snippet successfully")
    }

    @Test
    fun `can analyze snippet`() {
        val content = parseService.analyze(1L)
        assert(content == "Analyzed snippet successfully")
    }

    @Test
    fun `can validate snippet`() {
        val content = parseService.validate(1L)
        assert(content == "Validate snippet successfully")
    }

    @Test
    fun `can execute snippet`() {
        val content = parseService.execute(1L)
        assert(content == "Executed snippet successfully")
    }
}
