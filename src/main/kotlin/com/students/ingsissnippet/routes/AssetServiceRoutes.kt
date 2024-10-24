package com.students.ingsissnippet.routes

interface AssetServiceRoutes {
    fun get(id: Long): String
    fun put(id: Long, content: String): String
    fun delete(id: Long)
}
