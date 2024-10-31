package com.students.ingsissnippet.controllers

import com.students.ingsissnippet.dtos.response_dtos.TestDTO
import com.students.ingsissnippet.services.PermissionService
import com.students.ingsissnippet.services.TestService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@RestController
@RequestMapping("/api/tests")
class TestController(
    private val testService: TestService,
) {
    @GetMapping("/snippet/{snippetId}")
    fun getTestsBySnippetId(@PathVariable snippetId: Long): ResponseEntity<List<TestDTO>> {
        val tests = testService.getTestsBySnippetId(snippetId)
        return ResponseEntity.ok(tests)
    }

    @PostMapping("/snippet/{snippetId}")
    fun addTestToSnippet(
        @PathVariable snippetId: Long,
        @RequestBody testInput: Map<String, String>
    ): ResponseEntity<TestDTO> {
        val testDTO = testService.addTestToSnippet(
            snippetId,
            input = testInput["input"] ?: "",
            output = testInput["output"] ?: ""
        )
        return ResponseEntity.ok(testDTO)
    }
}