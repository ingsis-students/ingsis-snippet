package com.students.ingsissnippet.dtos.request_dtos

data class TestParseDTO(
    override val version: String,
    override val code: String,
    val inputs: List<String>,
    val outputs: List<String>
) : DTO
