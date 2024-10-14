package com.students.ingsissnippet.stubs

import com.students.ingsissnippet.fixture.UserFixtures
import com.students.ingsissnippet.routes.PermissionServiceRoutes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseEntity

@Profile("test")
@Configuration
class PermissionsApiTestConfiguration {

    @Bean
    @Primary
    fun createTestPermissionsApi(): PermissionServiceRoutes {
        return InMemoryPermissionsApi()
    }
}

class InMemoryPermissionsApi : PermissionServiceRoutes {

    private var permissionDb = UserFixtures.all()

    override fun checkIfOwner(snippetId: Long, email: String): Boolean {
        return permissionDb.any { it.ownerEmail == email && it.snippetIds.any { it == snippetId } }
    }

    override fun addSnippetToUser(email: String, snippetId: Long, role: String) {
        permissionDb.find { it.ownerEmail == email && it.snippetIds.add(snippetId) }
    }

    override fun shareSnippet(snippetId: Long, fromEmail: String, toEmail: String): ResponseEntity<String> {
        permissionDb.find { it.ownerEmail == fromEmail && it.snippetIds.add(snippetId) }
        return ResponseEntity.ok("Snippet shared with $toEmail")
    }
}
