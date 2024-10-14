package com.students.ingsissnippet.routes

import com.students.ingsissnippet.entities.Snippet
import org.springframework.http.ResponseEntity

/** This class is intended to have an overview of the SnippetService. */
interface SnippetServiceRoutes {
    /**Route to create a snippet and add that snippet to its creator*/
    fun createSnippet(name: String, content: String, language: String, owner: String): Snippet
    /** Route to get a specific snippet by its id */
    fun getSnippetOfId(id: Long): Snippet
    /** Route to edit a snippet if it exists, else it throws a @NoSuchElementException */
    fun editSnippet(id: Long, content: String): Snippet?
    /** Route to edit a snippet if it exists, else it throws a @NoSuchElementException */
    fun deleteSnippet(id: Long)
    /** Route that uses parse service to execute a snippet of code */
    fun executeSnippet(id: Long): String
    /** Route that uses parse service to analyze a snippet of code */
    fun analyzeSnippet(id: Long): String
    /** Route that uses parse service to format a snippet of code */
    fun formatSnippet(id: Long): String
    /** Route that uses permission service to share a snippet of code to other user*/
    fun shareSnippet(snippetId: Long, fromEmail: String, toEmail: String): ResponseEntity<String>
    /** Route that uses permission service to check if the user is the owner of a snippet */
    fun checkIfOwner(snippetId: Long, email: String): Boolean
    /** Route that uses parse service to check if a snippet compiles and returns errors */
    fun validateSnippet(id: Long): String
    /** Route that uses permission service to add a snippet to a user as Guest or Owner */
    fun addSnippetToUser(email: String, id: Long, role: String)
}
