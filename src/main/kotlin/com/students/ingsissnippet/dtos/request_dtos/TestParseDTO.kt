package com.students.ingsissnippet.dtos.request_dtos

data class TestParseDTO(
    val version: String,
    val snippetId: Long,
    val inputs: List<String>,
    val outputs: List<String>
)
