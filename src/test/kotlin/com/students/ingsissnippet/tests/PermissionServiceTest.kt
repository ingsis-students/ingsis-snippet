package com.students.ingsissnippet.tests

import com.students.ingsissnippet.config.producers.RedisLinterRuleProducer
import com.students.ingsissnippet.dtos.request_dtos.DTO
import com.students.ingsissnippet.dtos.response_dtos.FullSnippet
import com.students.ingsissnippet.services.PermissionService
import org.junit.jupiter.api.BeforeEach
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
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
@ActiveProfiles("test")
class PermissionServiceTest {

    @Autowired
    lateinit var permissionService: PermissionService

    @MockBean
    lateinit var restTemplate: RestTemplate

    @MockBean
    private lateinit var redisLinterRuleProducer: RedisLinterRuleProducer

    @BeforeEach
    fun setup() {
        whenever(
            restTemplate.postForObject(
                argThat { url: String? -> url?.contains("add-snippet") == true },
                any<HttpEntity<DTO>>(),
                eq(String::class.java)
            )
        ).thenAnswer {
            "Added snippet to user successfully"
        }

        whenever(
            restTemplate.postForObject(
                argThat { url: String? -> url?.contains("check-owner") == true },
                any<HttpEntity<Map<String, Any>>>(),
                eq(String::class.java)
            )
        ).thenReturn("User is the owner of the snippet")
    }

    @Test
    fun `can check if owner`() {
        val content = permissionService.checkIfOwner(1L, "example@gmail.com", "token")
        assert(content)
    }

    @Test
    fun `can add snippet to user`() {
        permissionService.addSnippetToUser("token", "admin", 1L, "Owner")
    }

    @Test
    fun `can share snippet to user`() {
        val response = permissionService.shareSnippet("token", 1L, "example@gmail.com", "otherexample@gmail.com", FullSnippet())
        assertEquals("Snippet shared with otherexample@gmail.com", response.headers["Share-Status"]?.first())
    }
}
