package com.students.ingsissnippet.routes

import com.students.ingsissnippet.entities.request_dtos.DTO
import org.springframework.http.HttpEntity

/** This class is intended to have an overview of the Parse Service */
interface ParseServiceRoutes {
    /** Route that uses parse service to execute a snippet of code */
    fun executeSnippet(id: Long): String
    /** Route that uses parse service to analyze a snippet of code */
    fun analyzeSnippet(id: Long): String
    /** Route that uses parse service to format a snippet of code */
    fun formatSnippet(id: Long): String
    /** Route that uses parse service to check if a snippet compiles and returns errors */
    fun validateSnippet(id: Long): String
    /** Route that uses parse service to execute a post request */
    fun executePost(entity: HttpEntity<DTO>, route: String): String
}
