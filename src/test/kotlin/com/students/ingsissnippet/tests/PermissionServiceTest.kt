package com.students.ingsissnippet.tests

import com.students.ingsissnippet.repositories.SnippetRepository
import com.students.ingsissnippet.services.SnippetService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import kotlin.test.Test

@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration
@ContextConfiguration(classes = [SnippetService::class])
@ComponentScan(basePackages = ["com.students.ingsissnippet"])
class PermissionServiceTest {

    @Autowired
    //   lateinit var snippetService: SnippetService

    @MockBean
    lateinit var snippetRepository: SnippetRepository

    //   @MockBean
    //   lateinit var permissionService: InMemoryPermissionsApi

    //  @MockBean
    //   lateinit var restTemplate: RestTemplate

    @BeforeEach
    fun setup() {
    }

    @AfterEach
    fun tearDown() {
        snippetRepository.deleteAll()
    }

    @Test
    fun `can check if owner`() {
    }
}
