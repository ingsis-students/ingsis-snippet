package com.students.ingsissnippet.tests

import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.repositories.SnippetRepository
import com.students.ingsissnippet.services.SnippetService
import com.students.ingsissnippet.stubs.InMemoryPermissionsApi
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.assertThrows
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
class SnippetServiceTest {

    @Autowired
    lateinit var snippetService: SnippetService

    @MockBean
    lateinit var snippetRepository: SnippetRepository

    @MockBean
    lateinit var permissionService: InMemoryPermissionsApi

    @MockBean
    lateinit var restTemplate: RestTemplate

    @BeforeEach
    fun setup() {
        val snippet = Snippet(
            id = 1,
            name = "My Snippet",
            content = "println(\"Hello World!\");",
            language = "PrintScript",
            owner = "admin"
        )
        whenever(snippetRepository.existsById(any())).thenReturn(true)
        whenever(snippetRepository.findById(any())).thenReturn(Optional.of(snippet))
        whenever(snippetRepository.save(snippet)).thenReturn(snippet)
        whenever(
            restTemplate.postForObject(
                argThat { url: String -> url.contains("add-snippet") },
                any<HttpEntity<Map<String, Any>>>(),
                eq(String::class.java)
            )
        ).thenAnswer {
            permissionService.addSnippetToUser("admin", 1, "Owner")
            "Success"
        }
    }

    @Test
    fun `can get snippet by id`() {
        val snippet = snippetService.getSnippetOfId(1)
        assert(snippet.id == 1L)
        assert(snippet.name == "My Snippet")
        assert(snippet.content == "println(\"Hello World!\");")
        assert(snippet.language == "PrintScript")
        assert(snippet.owner == "admin")
    }

    @Test
    fun `should return empty optional when id does not exist`() {
        whenever(snippetRepository.findById(999)).thenReturn(Optional.empty())
        assertThrows(NoSuchElementException::class.java) { snippetService.getSnippetOfId(999) }
    }

    @Test
    fun `can edit snippet`() {
        val updatedSnippet = snippetService.editSnippet(1, "println(\"Hello NEW World!\")")
        assert(updatedSnippet?.content == "println(\"Hello NEW World!\")")
    }

    @Test
    fun `can create snippet`() {
        val snippet = snippetService.createSnippet("My Snippet", "println(\"Hello World!\");", "PrintScript", "admin")

        assert(snippet.name == "My Snippet")
        assert(snippet.content == "println(\"Hello World!\");")
        assert(snippet.language == "PrintScript")
        assert(snippet.owner == "admin")
    }

    @AfterEach
    fun tearDown() {
        snippetRepository.deleteAll()
    }
}
