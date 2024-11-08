package com.students.ingsissnippet.simple_tests

import com.students.ingsissnippet.config.producers.RedisFormatRuleProducer
import com.students.ingsissnippet.config.producers.RedisLinterRuleProducer
import com.students.ingsissnippet.controllers.SnippetController
import com.students.ingsissnippet.dtos.response_dtos.FullSnippet
import com.students.ingsissnippet.entities.Language
import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.services.LanguageService
import com.students.ingsissnippet.services.SnippetService
import com.students.ingsissnippet.services.TestService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.hamcrest.Matchers.containsString
import org.springframework.context.annotation.ComponentScan
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@WebMvcTest(SnippetController::class)
@ActiveProfiles("test")
@ContextConfiguration(classes = [SnippetService::class])
@ComponentScan(basePackages = ["com.students.ingsissnippet"])
class WebMockTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var service: SnippetService

    @MockBean
    private lateinit var redisLinterRuleProducer: RedisLinterRuleProducer // initialize producer in test

    @MockBean
    private lateinit var redisFormatRuleProducer: RedisFormatRuleProducer

    @MockBean
    private lateinit var languageService: LanguageService

    @MockBean
    private lateinit var testService: TestService

    @Test
    @WithMockUser(authorities = ["SCOPE_read:snippets"])
    fun mockTest() {
        val language = Language(
            id = 1,
            name = "printscript",
            extension = "prs",
            version = "1.0",
        )
        val snippet = Snippet(
            id = 1,
            name = "My Snippet",
            owner = "admin",
            language = language
        )
        `when`(service.get(1L)).thenReturn(
            FullSnippet(
                snippet = snippet,
                content = "println(\"New edited world!\");"
            )
        )

        mockMvc.perform(get("/api/snippets/1"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(content().string(containsString("New edited world")))
    }
}
