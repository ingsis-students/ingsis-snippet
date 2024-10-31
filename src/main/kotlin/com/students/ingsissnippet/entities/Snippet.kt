package com.students.ingsissnippet.entities

import com.fasterxml.jackson.annotation.JsonBackReference
import com.students.ingsissnippet.dtos.request_types.Compliance.PENDING
import com.students.ingsissnippet.dtos.request_types.Compliance
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
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
    val compilance: Compliance = PENDING,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id", nullable = false)
    @JsonBackReference
    val language: Language
) {
    constructor() : this(0, "", "", PENDING, Language())
}
