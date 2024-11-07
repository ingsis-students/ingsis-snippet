package com.students.ingsissnippet.dtos.response_dtos

import com.students.ingsissnippet.dtos.request_types.Compliance

class SnippetWithRole(snippet: FullSnippet, val role: String) {
    val id: Long = snippet.id
    val name: String = snippet.name
    val owner: String = snippet.owner
    val language: String = snippet.language
    val extension: String = snippet.extension
    val compilance: Compliance = snippet.compilance
    val version: String = snippet.version

    constructor() : this(FullSnippet(), "")
}
