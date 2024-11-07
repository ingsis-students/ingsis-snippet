package com.students.ingsissnippet.factories

import com.students.ingsissnippet.dtos.request_types.Rule

object RuleFactory {
    fun defaultLintRules(): List<Rule> {
        return listOf(
            Rule(id = "1", name = "UnusedVariableCheck", isActive = false),
            Rule(id = "2", name = "NamingFormatCheck", isActive = false, value = "camelCase"),
            Rule(id = "3", name = "PrintUseCheck", isActive = false),
            Rule(id = "4", name = "ReadInputCheck", isActive = false)
        )
    }

    fun defaultFormatRules(): List<Rule> {
        return listOf(
            Rule(id = "3", name = "Rule 3", isActive = false),
            Rule(id = "4", name = "Rule 4", isActive = false),
        )
    }
}
