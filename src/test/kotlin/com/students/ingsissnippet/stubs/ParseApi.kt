package com.students.ingsissnippet.stubs

import com.students.ingsissnippet.entities.Snippet
import org.springframework.context.annotation.Profile

@Profile("test")
class ParseApi {
    fun format(id: Long): String {
        return "Formatted Code of $id successfully"
    }
    fun analyzeSnippet(id: Long): String {
        return "Analyzed snippet of $id successfully"
    }
    fun validateSnippet(id: Long): Snippet {
        return Snippet()
    }
}
