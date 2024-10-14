package com.students.ingsissnippet.controllers

import com.students.ingsissnippet.entities.Snippet
import com.students.ingsissnippet.services.SnippetService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/snippets")
class SnippetController(
    private val snippetService: SnippetService,
) {

    @GetMapping("/get/{id}")
    fun getSnippet(@PathVariable id: Long): Snippet {
        return snippetService.getSnippetOfId(id)
    }

    @PostMapping("/create")
    fun createSnippet(@RequestBody snippetRequest: SnippetRequest): Snippet {
        return snippetService.createSnippet(
            snippetRequest.name, snippetRequest.content, snippetRequest.language, snippetRequest.owner
        )
    }

    @PostMapping("/edit/{id}")
    fun editSnippet(@PathVariable id: Long, @RequestBody req: ContentRequest): Snippet? {
        return snippetService.editSnippet(id, req.content)
    }

    @PostMapping("/delete/{id}")
    fun deleteSnippet(@PathVariable id: Long) {
        snippetService.deleteSnippet(id)
    }

    @PostMapping("/format/{id}")
    fun formatSnippet(@PathVariable id: Long): Snippet? {
        val formattedCode = snippetService.formatSnippet(id)

        // Edit the snippet with the formatted code to save it ~ ¿We want this?
        return snippetService.editSnippet(id, formattedCode)
    }

    @PostMapping("/execute/{id}")
    fun executeSnippet(@PathVariable id: Long): String {
        return snippetService.executeSnippet(id)
    }

    @PostMapping("/validate/{id}")
    fun validateSnippet(@PathVariable id: Long): String {
        return snippetService.validateSnippet(id)
    }

    @PostMapping("/lint/{id}")
    fun lintSnippet(@PathVariable id: Long): String {
        return snippetService.analyzeSnippet(id)
    }

    @PostMapping("/share/{id}")
    fun shareSnippet(@PathVariable id: Long, @RequestBody emails: ShareRequest): ResponseEntity<String> {
        return snippetService.shareSnippet(id, emails.fromEmail, emails.toEmail)
    }
}

data class SnippetRequest(
    val name: String,
    val content: String,
    val language: String,
    val owner: String
)

data class ContentRequest(val content: String)

data class ShareRequest(
    val fromEmail: String,
    val toEmail: String
)
