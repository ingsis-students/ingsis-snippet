package com.students.ingsissnippet.entities.request_dtos

import com.fasterxml.jackson.databind.JsonNode

data class FormatDTO(
    override val version: String,
    override val code: String,
    val rules: JsonNode
) : DTO
