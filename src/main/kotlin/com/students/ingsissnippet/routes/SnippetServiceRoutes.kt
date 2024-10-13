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
    fun analyzeSnippet(id: Long): String
    fun formatSnippet(id: Long): String
    fun shareSnippet(snippetId: Long, fromEmail: String, toEmail: String): ResponseEntity<String>
    fun checkIfOwner(snippetId: Long, email: String): Boolean
    fun validateSnippet(id: Long): Snippet
    fun addSnippetToUser(email: String, id: Long, role: String)
}
