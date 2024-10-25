package com.students.ingsissnippet.dtos.request_types

import com.students.ingsissnippet.entities.Language

data class SnippetRequest(
    val name: String,
    val content: String,
    val owner: String,
    val language: Language
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
