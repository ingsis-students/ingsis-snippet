package com.students.ingsissnippet.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.OneToMany
import jakarta.persistence.Id

@Entity
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long,
    val name: String,
    val email: String,
    val password: String,
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val snippets: List<Snippet>
) {
    constructor() : this(0, "", "", "", emptyList())
}
