package com.students.ingsissnippet.config

data class SnippetMessage(
    val snippetId: Long,
    val userId: Long,
    val version: String,
    val jwtToken: String
)
