package com.students.ingsissnippet

import com.students.ingsissnippet.config.producers.RedisLinterRuleProducer
import com.students.ingsissnippet.services.SnippetService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class IngsisSnippetApplicationTests {

    @Autowired
    lateinit var snippetService: SnippetService

    @MockBean
    private lateinit var redisLinterRuleProducer: RedisLinterRuleProducer

    @Test
    fun contextLoads() {
        assertThat(snippetService).isNotNull
    }
}
