package com.students.ingsissnippet.dtos.response_dtos

import com.students.ingsissnippet.entities.Snippet

class FullSnippet(snippet: Snippet, val content: String) {
    val id: Long = snippet.id
    val name: String = snippet.name
    val owner: String = snippet.owner
    val language: String = snippet.language.name
    val extension: String = snippet.language.extension
    val version: String = snippet.language.version
}
