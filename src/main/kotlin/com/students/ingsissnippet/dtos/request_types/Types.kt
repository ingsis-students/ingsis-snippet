package com.students.ingsissnippet.dtos.request_types

data class SnippetRequest(
    val name: String,
    val content: String,
    val languageId: String,
    val owner: String
)

data class ContentRequest(val content: String)

data class ShareRequest(
    val fromEmail: String,
    val toEmail: String
)

data class Rule(
    val id: String,
    val name: String,
    val isActive: Boolean,
    val value: Any? = null
)
