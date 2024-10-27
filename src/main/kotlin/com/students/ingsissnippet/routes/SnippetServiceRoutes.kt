package com.students.ingsissnippet.routes

import com.students.ingsissnippet.dtos.response_dtos.FullSnippet
import com.students.ingsissnippet.entities.Language

/** This class is intended to have an overview of the SnippetService. */
interface SnippetServiceRoutes {
    /**Route to create a snippet and add that snippet to its creator*/
    fun create(name: String, content: String, language: Language, owner: String): FullSnippet
    /** Route to get a specific snippet by its id */
    fun get(id: Long): FullSnippet
    /** Route to edit a snippet if it exists, else it throws a @SnippetNotFound exception **/
    fun update(id: Long, content: String): FullSnippet?
    /** Route to delete a snippet if it exists, else it throws a @SnippetNotFound exception */
    fun delete(id: Long)
    /** Route to check if a snippet exists, if it doesn't it throws a @SnippetNotFound exception */
    fun checkIfExists(id: Long, operation: String)
}
