package com.students.ingsissnippet.factories

import com.students.ingsissnippet.dtos.request_types.Rule

object RuleFactory {
    fun defaultLintRules(): List<Rule> {
        return listOf(
            Rule(id = "1", name = "Rule 1", isActive = false),
            Rule(id = "2", name = "Rule 2", isActive = false),
        )
    }
}