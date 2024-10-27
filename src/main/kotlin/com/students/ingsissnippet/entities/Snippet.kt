package com.students.ingsissnippet.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn

@Entity
data class Snippet(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long = 0,
    val name: String,
    val owner: String,

    @ManyToOne
    @JoinColumn(name = "language_id")
    val language: Language? = null
) {
    constructor() : this(0, "", "", null)
}
