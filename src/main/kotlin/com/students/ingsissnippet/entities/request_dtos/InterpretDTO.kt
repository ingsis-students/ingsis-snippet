package com.students.ingsissnippet.entities.request_dtos

data class InterpretDTO(
    override val version: String,
    override val code: String,
) : DTO
