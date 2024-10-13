package com.students.ingsissnippet.clients

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

// @FeignClient(value = "permissions", url = "http://infra-permissions-api:8080/")
interface PermissionsClient {

    @RequestMapping(method = [RequestMethod.GET], value = ["/api/user/"])
    fun hasPermission(
        @RequestParam("type") type: String,
        @RequestParam("snippetId") snippetId: Long,
        @RequestParam("userId") userId: Long
    ): ResponseEntity<Boolean>

    @RequestMapping(method = [RequestMethod.POST], value = ["/api/user/"])
    fun addSnippet(
        @RequestParam("snippetId") snippetId: Long,
        @RequestParam("userId") userId: Long
    ): ResponseEntity<String>
}
