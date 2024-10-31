package com.students.ingsissnippet.services

import com.students.ingsissnippet.dtos.response_dtos.TestDTO
import com.students.ingsissnippet.entities.Test
import com.students.ingsissnippet.repositories.SnippetRepository
import com.students.ingsissnippet.repositories.TestRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class TestService(
    private val testRepository: TestRepository,
    private val snippetRepository: SnippetRepository
) {

    fun getTestsBySnippetId(snippetId: Long): List<TestDTO> {
        val tests = testRepository.findBySnippetId(snippetId)
        return tests.map { TestDTO(it) }
    }

    @Transactional
    open fun addTestToSnippet(snippetId: Long, input: String, output: String): TestDTO {
        val snippet = snippetRepository.findById(snippetId)
            .orElseThrow { IllegalArgumentException("Snippet not found") }

        val test = Test(input = input, output = output, snippet = snippet)
        testRepository.save(test)
        return TestDTO(test)
    }
}
