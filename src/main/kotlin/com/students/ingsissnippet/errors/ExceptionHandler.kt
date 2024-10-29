package com.students.ingsissnippet.errors

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(SnippetNotFound::class)
    fun handle(ex: SnippetNotFound): ResponseEntity<Any> {
        return ResponseEntity.notFound().build()
    }

    @ExceptionHandler(LanguageNotFound::class)
    fun handle(ex: LanguageNotFound): ResponseEntity<Any> {
        return ResponseEntity.notFound().build()
    }
}
