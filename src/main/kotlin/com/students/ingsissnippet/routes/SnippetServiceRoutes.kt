package com.students.ingsissnippet.routes

import com.students.ingsissnippet.entities.Snippet

/** This class is intended to have an overview of the SnippetService. */
interface SnippetServiceRoutes {
    /**Route to create a snippet and add that snippet to its creator*/
    fun createSnippet(name: String, content: String, language: String, owner: String): Snippet
    /** Route to get a specific snippet by its id */
    fun getSnippetOfId(id: Long): Snippet
    /** Route to edit a snippet if it exists, else it throws a @SnippetNotFound exception */
    fun editSnippet(id: Long, content: String): Snippet?
    /** Route to edit a snippet if it exists, else it throws a @SnippetNotFound exception */
    fun deleteSnippet(id: Long)
    /** Route to check if a snippet exists, if it doesn't it throws a @SnippetNotFound exception */
    fun checkIfExists(id: Long, operation: String)
    fun getByUser(id: Long): List<Snippet>
}
