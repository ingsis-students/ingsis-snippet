package com.students.ingsissnippet.routes

import com.students.ingsissnippet.dtos.request_dtos.DTO
import org.springframework.http.HttpEntity

/** This class is intended to have an overview of the Parse Service */
interface ParseServiceRoutes {
    /** Route that uses parse service to execute a snippet of code */
    fun execute(id: Long): String

    /** Route that uses parse service to analyze a snippet of code */
    fun analyze(id: Long): String

    /** Route that uses parse service to format a snippet of code */
    fun format(version: String, content: String, rules: String, token: String): String

    /** Route that uses parse service to check if a snippet compiles and returns errors */
    fun validate(token: String, version: String, content: String): List<String>

    /** Route that uses parse service to execute a post request */
    fun test(token: String, snippetId: Long, inputs: List<String>, outputs: List<String>): List<String>

    fun executePost(entity: HttpEntity<DTO>, route: String): String
}
