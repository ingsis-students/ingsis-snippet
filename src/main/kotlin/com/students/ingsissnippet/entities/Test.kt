package com.students.ingsissnippet.entities

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
data class Test(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long = 0,
    val name: String = "",

    @ElementCollection
    val input: List<String> = listOf(),

    @ElementCollection
    val output: List<String> = listOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snippet_id", nullable = false)
    @JsonBackReference
    val snippet: Snippet
) {
    constructor() : this(0, "", listOf(), listOf(), Snippet())
}