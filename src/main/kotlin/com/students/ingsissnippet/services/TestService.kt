package com.students.ingsissnippet.services

import com.students.ingsissnippet.dtos.response_dtos.TestDTO
import com.students.ingsissnippet.entities.Test
import com.students.ingsissnippet.repositories.SnippetRepository
import com.students.ingsissnippet.repositories.TestRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class TestService(
    private val testRepository: TestRepository,
    private val snippetRepository: SnippetRepository,
    private val parseService: ParseService,
) {

    fun getTestsBySnippetId(snippetId: Long): List<TestDTO> {
        val tests = testRepository.findBySnippetId(snippetId)
        return tests.map { TestDTO(it) }
    }

    fun getTestById(id: Long): Test {
        return testRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Test not found") }
    }

    fun addTestToSnippet(snippetId: Long, name: String, input: List<String>, output: List<String>): TestDTO {
        val snippet = snippetRepository.findById(snippetId)
            .orElseThrow { IllegalArgumentException("Snippet not found") }
        val test = Test(name = name, input = input, output = output, snippet = snippet)
        testRepository.save(test)
        return TestDTO(test)
    }

    fun deleteTestById(id: Long) {
        testRepository.deleteById(id)
    }

    fun executeTest(token: String, testId: Long): ResponseEntity<String> {
        val test = getTestById(testId)
        return parseService.test(token, test.snippet.id, test.input, test.output)
    }

    fun executeAllSnippetTests(token: String, snippetId: Long): ResponseEntity<String> {
        val tests = getTestsBySnippetId(snippetId)
        val responses = tests.map { executeTest(token, it.id) }
        return if (responses.all { it.statusCode.is2xxSuccessful }) {
            ResponseEntity.ok("success")
        } else {
            ResponseEntity.ok("fail")
        }
    }

}
