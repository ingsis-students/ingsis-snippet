package com.students.ingsissnippet.dtos.response_dtos

import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.dtos.request_types.Compliance
import org.springframework.aot.generate.Generated

@Generated
open class SnippetDTO(snippet: Snippet) {
    val id: Long = snippet.id
    val name: String = snippet.name
    val owner: String = snippet.owner
    val language: String = snippet.language.name
    val extension: String = snippet.language.extension
    val compilance: Compliance = snippet.status
    val version: String = snippet.language.version
}
