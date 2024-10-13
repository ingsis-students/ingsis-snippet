package com.students.ingsissnippet.stubs

import com.students.ingsissnippet.fixture.UserFixtures
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@Profile("test")
@Configuration
class PermissionsApiTestConfiguration {

    @Bean
    @Primary
    fun createTestPermissionsApi(): PermissionsApi {
        return InMemoryPermissionsApi()
    }
}

class InMemoryPermissionsApi : PermissionsApi {

    private var permissionDb = UserFixtures.all()

    override fun checkIfOwner(snippetId: Long, email: String): Boolean {
        return permissionDb.any { it.ownerEmail == email && it.snippetIds.any { it == snippetId } }
    }

    override fun addSnippetToUser(owner: String, snippetId: Long, role: String) {
        val owner = permissionDb.find { it.ownerEmail == owner }
        owner?.snippetIds?.add(snippetId)
    }
}

interface PermissionsApi {
    fun checkIfOwner(snippetId: Long, email: String): Boolean
    fun addSnippetToUser(owner: String, snippetId: Long, role: String)
}
