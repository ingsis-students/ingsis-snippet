package com.students.ingsissnippet.tests

import com.students.ingsissnippet.entities.Language
import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.repositories.SnippetRepository
import com.students.ingsissnippet.services.AssetService
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
    private lateinit var assetService: AssetService

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
        whenever(assetService.get(1)).thenReturn("println(\"Hello World!\")")
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

    @AfterEach
    fun tearDown() {
        snippetRepository.deleteAll()
    }

    @Test
    fun `can get snippet by id`() {
        val snippet = snippetService.get(1)
        assert(snippet.id == 1L)
        assert(snippet.name == "My Snippet")
        assert(snippet.content == "println(\"Hello World!\")")
        assert(snippet.language == "printscript")
        assert(snippet.version == "1.0")
        assert(snippet.owner == "admin")
    }

    @Test
    fun `should return empty optional when id does not exist`() {
        whenever(snippetRepository.findById(999)).thenReturn(Optional.empty())
        assertThrows(NoSuchElementException::class.java) { snippetService.get(999) }
    }

    @Test
    fun `can edit snippet`() {
        val updatedSnippet = snippetService.update(1, "println(\"Hello NEW World!\")")
        assert(updatedSnippet.content == "println(\"Hello NEW World!\")")
    }

    @Test
    fun `can create snippet`() {
        val language = Language(
            id = 1,
            name = "printscript",
            version = "1.0"
        )
        val snippet = snippetService.create(
            name = "My Snippet",
            content = "println(\"Hello World!\");",
            language = language,
            owner = "admin"
        )

        assert(snippet.name == "My Snippet")
        assert(snippet.content == "println(\"Hello World!\");")
        assert(snippet.language == "printscript")
        assert(snippet.owner == "admin")
    }
}
