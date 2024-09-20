package com.students.ingsissnippet

import com.students.ingsissnippet.controllers.SnippetController
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SmokeTest {

    @Autowired
    private lateinit var controller: SnippetController

    @Test
    fun contextLoads() {
        assertThat(controller).isNotNull
    }
}
