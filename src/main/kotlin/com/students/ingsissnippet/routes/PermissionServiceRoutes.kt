package com.students.ingsissnippet.routes

import org.springframework.http.ResponseEntity

/** This class is intended to have an overview of the Permissions Service */
interface PermissionServiceRoutes {
    /** This method checks if a snippet user of @email is owner of the snippet with id @snippetId */
    fun checkIfOwner(snippetId: Long, email: String): Boolean
    /** This method is used to add a snippet to a certain user of @email with the specified @role, Guest or Owner */
    fun addSnippetToUser(email: String, snippetId: Long, role: String)
    /** Route that uses permission service to share a snippet of code to other user*/
    fun shareSnippet(snippetId: Long, fromEmail: String, toEmail: String): ResponseEntity<String>
}
