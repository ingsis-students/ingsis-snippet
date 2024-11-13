package com.students.ingsissnippet.language

import com.fasterxml.jackson.databind.ObjectMapper
import com.students.ingsissnippet.config.producers.RedisFormatRuleProducer
import com.students.ingsissnippet.config.producers.RedisLinterRuleProducer
import com.students.ingsissnippet.entities.Language
import com.students.ingsissnippet.repositories.LanguageRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class LanguageTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var languageRepository: LanguageRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var redisLinterRuleProducer: RedisLinterRuleProducer

    @MockBean
    private lateinit var redisFormatRuleProducer: RedisFormatRuleProducer

    @BeforeEach
    fun setup() {
        languageRepository.deleteAll()
        val language = Language(
            name = "printscript",
            version = "1.0",
            extension = "prs"
        )
        languageRepository.save(language)
    }

    @AfterEach
    fun teardown() {
        languageRepository.deleteAll()
    }

    @Test
    @WithMockUser
    @Transactional
    fun `test get all languages`() {
        mockMvc.perform(get("/api/languages/all"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].name").value("printscript"))
            .andExpect(jsonPath("$[0].version").value("1.0"))
            .andExpect(jsonPath("$[0].extension").value("prs"))
    }

    @Test
    @WithMockUser
    @Transactional
    fun `test create language`() {
        val newLanguage = Language(
            name = "kotlin",
            version = "1.5",
            extension = "kt"
        )
        val newLanguageJson = objectMapper.writeValueAsString(newLanguage)

        mockMvc.perform(
            post("/api/languages/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newLanguageJson)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("kotlin"))
            .andExpect(jsonPath("$.version").value("1.5"))
            .andExpect(jsonPath("$.extension").value("kt"))
    }
}
