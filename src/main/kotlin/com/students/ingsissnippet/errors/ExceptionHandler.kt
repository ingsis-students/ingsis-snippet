package com.students.ingsissnippet.errors

import org.springframework.aot.generate.Generated
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@Generated
@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(SnippetNotFound::class)
    fun handle(ex: SnippetNotFound): ResponseEntity<Any> {
        return ResponseEntity.badRequest().body(ex.message)
    }

    @ExceptionHandler(LanguageNotFound::class)
    fun handle(ex: LanguageNotFound): ResponseEntity<Any> {
        return ResponseEntity.badRequest().body(ex.message)
    }

    @ExceptionHandler(TestNotFound::class)
    fun handle(ex: TestNotFound): ResponseEntity<Any> {
        return ResponseEntity.badRequest().body(ex.message)
    }
}
