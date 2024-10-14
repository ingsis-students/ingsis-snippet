package com.students.ingsissnippet.simple_tests

import com.students.ingsissnippet.controllers.SnippetController
import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.services.SnippetService
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

    @Test
    fun mockTest() {

        `when`(service.getSnippetOfId(1L)).thenReturn(
            Snippet(
                1,
                "My Snippet",
                "println(\"New edited world!\");",
                "PrintScript",
                "admin"
            )
        )

        mockMvc.perform(get("/snippets/get/1"))
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(content().string(containsString("My Snippet")))
    }
}
