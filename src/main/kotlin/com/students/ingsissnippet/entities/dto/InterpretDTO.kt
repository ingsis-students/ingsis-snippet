package com.students.ingsissnippet.entities.dto

data class InterpretDTO(
    override val version: String,
    override val code: String,
) : DTO
