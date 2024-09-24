package com.students.ingsissnippet.entities.dto

import com.fasterxml.jackson.databind.JsonNode

data class LinterDTO(
    override val version: String,
    override val code: String,
    val rules: Map<String, JsonNode>
) : DTO
