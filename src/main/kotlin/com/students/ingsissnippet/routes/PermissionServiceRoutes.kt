package com.students.ingsissnippet.routes

import com.students.ingsissnippet.entities.Snippet
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity

/** This class is intended to have an overview of the Permissions Service */
interface PermissionServiceRoutes {
    /** This method checks if a snippet user of @email is owner of the snippet with id @snippetId */
    fun checkIfOwner(snippetId: Long, email: String): Boolean
    /** This method is used to add a snippet to a certain user of @email with the specified @role, Guest or Owner */
    fun addSnippetToUser(email: String, snippetId: Long, role: String)
    /** Route that uses permission service to share a snippet of code to other user*/
    fun shareSnippet(snippetId: Long, fromEmail: String, toEmail: String): ResponseEntity<String>
    /** This method is used to execute a post request to the permission service */
    fun executePost(entity: HttpEntity<Map<String, Any>>, string: String): String?

    fun validate(jwt: String): ResponseEntity<Long>
    fun getSnippets(id: Long): ResponseEntity<List<Snippet>>
}
