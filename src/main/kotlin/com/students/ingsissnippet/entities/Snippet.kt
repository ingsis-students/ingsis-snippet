package com.students.ingsissnippet.entities

class Snippet(
    val id: Long,
    val name: String,
    val content: String,
    val lenguaje: String,
    val historial: List<Snippet>,
    val owner: String,
    val guests: List<User>
)

class User (
    val id: Long,
    val name: String,
    val email: String,
    val password: String,
    val snippets: List<Snippet>
)