package com.students.ingsissnippet.entities

import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class Snippet(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,
    val name: String,
    val content: String,
    val language: String,
    val owner: String,
    @ElementCollection
    val guests: List<String> = emptyList()
) {
    constructor() : this(0, "", "", "", "", emptyList())
}
