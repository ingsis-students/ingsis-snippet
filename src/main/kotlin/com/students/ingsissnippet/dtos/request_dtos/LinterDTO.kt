package com.students.ingsissnippet.dtos.request_dtos

import com.fasterxml.jackson.databind.JsonNode

data class LinterDTO(
    override val version: String,
    override val code: String,
    val rules: Map<String, JsonNode>
) : DTO
