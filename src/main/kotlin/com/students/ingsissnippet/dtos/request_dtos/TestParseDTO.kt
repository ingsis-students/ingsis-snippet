package com.students.ingsissnippet.dtos.request_dtos

import org.springframework.aot.generate.Generated

@Generated
data class TestParseDTO(
    val version: String,
    val snippetId: Long,
    val inputs: List<String>,
    val outputs: List<String>
)
