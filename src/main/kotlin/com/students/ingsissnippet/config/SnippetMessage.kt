package com.students.ingsissnippet.config

import kotlinx.serialization.json.JsonObject

data class SnippetMessage(
    val snippetId: Long,
    val rules: JsonObject
)
