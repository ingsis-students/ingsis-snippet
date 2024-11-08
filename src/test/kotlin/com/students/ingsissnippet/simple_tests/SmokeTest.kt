package com.students.ingsissnippet.simple_tests

import com.students.ingsissnippet.config.producers.RedisFormatRuleProducer
import com.students.ingsissnippet.config.producers.RedisLinterRuleProducer
import com.students.ingsissnippet.controllers.SnippetController
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
class SmokeTest {

    @Autowired
    private lateinit var controller: SnippetController

    @MockBean
    private lateinit var redisLinterRuleProducer: RedisLinterRuleProducer

    @MockBean
    private lateinit var redisFormatRuleProducer: RedisFormatRuleProducer

    @Test
    fun contextLoads() {
        assertThat(controller).isNotNull
    }
}
