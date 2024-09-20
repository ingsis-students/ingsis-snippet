package com.students.ingsissnippet.controllers

import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.services.SnippetService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/snippets")
class SnippetController(private val snippetService: SnippetService) {

    @GetMapping("/get/{id}")
    fun getSnippet(@PathVariable id: Long): Snippet {
        return snippetService.getSnippetOfId(id)
    }

    @PostMapping("/create")
    fun createSnippet(@RequestBody snippetRequest: SnippetRequest): Snippet {
        return snippetService.createSnippet(
            snippetRequest.name, snippetRequest.content, snippetRequest.language
        )
    }

    @PostMapping("/edit/{id}")
    fun editSnippet(
        @PathVariable id: Long,
        @RequestBody req: ContentRequest
    ): Snippet? {
        return snippetService.editSnippet(id, req.content)
    }
}

data class SnippetRequest(
    val name: String,
    val content: String,
    val language: String
)

data class ContentRequest(val content: String)
