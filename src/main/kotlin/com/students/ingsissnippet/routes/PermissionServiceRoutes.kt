package com.students.ingsissnippet.routes

import com.students.ingsissnippet.dtos.response_dtos.FullSnippet
import com.students.ingsissnippet.dtos.response_dtos.SnippetUserDto
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity

/** This class is intended to have an overview of the Permissions Service */
interface PermissionServiceRoutes {
    /** This method checks if a snippet user of @email is owner of the snippet with id @snippetId */
    fun checkIfOwner(snippetId: Long, email: String, token: String): Boolean
    /** This method is used to add a snippet to a certain user of @email with the specified @role, Guest or Owner */
    fun addSnippetToUser(token: String, email: String, snippetId: Long, role: String)
    /** This method is used to get the snippets of a certain user */
    fun getSnippetsOfUser(token: String, email: String): List<SnippetUserDto>
    /** Route that uses permission service to share a snippet of code to other user*/
    fun shareSnippet(token: String, snippetId: Long, fromEmail: String, toEmail: String, snippet: FullSnippet): ResponseEntity<FullSnippet>
    /** This method is used to execute a post request to the permission service */
    fun executePost(entity: HttpEntity<Map<String, Any>>, string: String): String?

    fun validate(jwt: String): ResponseEntity<Long>
    fun getSnippetsId(jwt: String, id: Long): ResponseEntity<List<Long>>
}
