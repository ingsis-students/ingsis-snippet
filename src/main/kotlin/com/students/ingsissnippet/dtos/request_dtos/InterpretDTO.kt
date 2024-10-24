package com.students.ingsissnippet.dtos.request_dtos

data class InterpretDTO(
    override val version: String,
    override val code: String,
) : DTO
