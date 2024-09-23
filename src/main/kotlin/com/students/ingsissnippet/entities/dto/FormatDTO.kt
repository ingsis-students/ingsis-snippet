package com.students.ingsissnippet.entities.dto

import com.fasterxml.jackson.databind.JsonNode

data class FormatDTO(
    override val version: String,
    override val code: String,
    val rules: JsonNode
) : DTO
