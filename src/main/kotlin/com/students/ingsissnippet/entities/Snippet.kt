package com.students.ingsissnippet.entities

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.students.ingsissnippet.dtos.request_types.Compliance
import com.students.ingsissnippet.dtos.request_types.Compliance.PENDING
import jakarta.persistence.*

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
    val language: Language,

    @OneToMany(mappedBy = "snippet", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JsonManagedReference
    val tests: List<Test> = emptyList()
) {
    constructor() : this(0, "", "", PENDING, Language(), emptyList())
}