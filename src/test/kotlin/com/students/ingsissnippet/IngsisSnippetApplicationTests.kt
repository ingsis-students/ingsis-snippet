package com.students.ingsissnippet

import com.students.ingsissnippet.services.SnippetService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class IngsisSnippetApplicationTests {

    @Autowired
    lateinit var snippetService: SnippetService

    @Test
    fun contextLoads() {
        assertThat(snippetService).isNotNull
    }
}
