package com.students.ingsissnippet.fixture

class PermissionDb {
    val ownerEmail: String
    var snippetIds: MutableList<Long>

    constructor(ownerEmail: String, snippetsIds: MutableList<Long>) {
        this.ownerEmail = ownerEmail
        this.snippetIds = snippetsIds
    }
}
