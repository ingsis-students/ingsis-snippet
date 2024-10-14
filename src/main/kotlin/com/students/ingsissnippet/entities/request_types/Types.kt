package com.students.ingsissnippet.entities.request_types

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
