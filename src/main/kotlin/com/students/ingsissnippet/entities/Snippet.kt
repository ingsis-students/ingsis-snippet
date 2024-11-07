package com.students.ingsissnippet.entities

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.students.ingsissnippet.dtos.request_types.Compliance
import com.students.ingsissnippet.dtos.request_types.Compliance.PENDING
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.CascadeType

@Entity
data class Snippet(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long = 0,
    val name: String,
    val owner: String,
    var status: Compliance = PENDING,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "language_id", nullable = false)
    @JsonBackReference
    val language: Language,

    @OneToMany(mappedBy = "snippet", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JsonManagedReference
    @JsonIgnore
    val tests: List<Test> = emptyList()
) {
    constructor() : this(0, "", "", PENDING, Language(), emptyList())

    override fun toString(): String {
        return "Snippet(id=$id, name='$name', owner='$owner', compilance=$status)"
    }
}
