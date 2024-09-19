package com.students.ingsissnippet.controllers

import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.services.SnippetService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class SnippetController() {
    private val snippetService: SnippetService = SnippetService()

    @GetMapping("/snippet")
    fun getSnippet(@RequestParam id: Long): Snippet {
        return snippetService.getSnippetOfId(id)
    }

    @PostMapping("/snippet")
    fun createSnippet(@RequestBody snippetRequest: SnippetRequest): Snippet {
        return snippetService.createSnippet(
            snippetRequest.name,
            snippetRequest.content,
            snippetRequest.language
        )
    }
}

data class SnippetRequest(
    val name: String,
    val content: String,
    val language: String
)