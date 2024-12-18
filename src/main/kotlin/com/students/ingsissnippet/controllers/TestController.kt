package com.students.ingsissnippet.controllers

import com.students.ingsissnippet.dtos.response_dtos.TestDTO
import com.students.ingsissnippet.services.TestService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestHeader

@RestController
@RequestMapping("/api/tests")
class TestController(
    private val testService: TestService,
) {
    @GetMapping("/snippet/{snippetId}")
    fun getTestsBySnippetId(@PathVariable snippetId: Long): ResponseEntity<List<TestDTO>> {
        val tests = testService.getTestsBySnippetId(snippetId)
        return ResponseEntity.ok(tests.map { TestDTO(it) })
    }

    @PostMapping("/snippet/{snippetId}")
    fun addTestToSnippet(
        @PathVariable snippetId: Long,
        @RequestBody testBody: Map<String, Any>
    ): ResponseEntity<TestDTO> {
        val testDTO = testService.addTestToSnippet(
            snippetId,
            name = testBody["name"] as? String ?: "",
            input = testBody["input"] as? List<String> ?: listOf(),
            output = testBody["output"] as? List<String> ?: listOf()
        )
        return ResponseEntity.ok(testDTO)
    }

    @DeleteMapping("/{id}")
    fun deleteTestById(@PathVariable id: Long): ResponseEntity<Void> {
        testService.deleteTestById(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/run")
    fun runTests(@RequestHeader("Authorization") token: String, @PathVariable id: Long): ResponseEntity<String> {
        return testService.executeTest(token, id)
    }

    @PostMapping("/{snippetId}/run-all")
    fun runAllTests(
        @RequestHeader("Authorization") token: String,
        @PathVariable snippetId: Long
    ): ResponseEntity<Map<String, List<String>>> {
        val testResults = testService.executeAllSnippetTests(token, snippetId)
        return ResponseEntity.ok(testResults)
    }
}
