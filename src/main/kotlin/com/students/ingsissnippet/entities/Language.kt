package com.students.ingsissnippet.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
data class Language(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0,
    val name: String,
    val version: String,
    val extension: String,

    @OneToMany(mappedBy = "language")
    val snippets: List<Snippet> = emptyList()
) {
    constructor() : this(0, "", "", "", emptyList())
}
