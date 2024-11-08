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
            Rule(id = "20", name = "OnlyOneSpacePermitted", isActive = true),
            Rule(id = "21", name = "NewLineAfterSemicolon", isActive = true),
            Rule(id = "22", name = "SpaceAfterAndBeforeOperators", isActive = true),
            Rule(id = "23", name = "NewLineAfterBrace", isActive = true),

            Rule(id = "1", name = "SpaceBeforeColon", isActive = false),
            Rule(id = "2", name = "SpaceAfterColon", isActive = false),
            Rule(id = "3", name = "NewlineAfterPrintln", isActive = false),
            Rule(id = "4", name = "NewlineBeforePrintln", isActive = false),
            Rule(id = "5", name = "SpaceAroundEquals", isActive = false),
            Rule(id = "6", name = "NoSpaceAroundEquals", isActive = false),
            Rule(id = "7", name = "NumberOfSpacesIndentation", isActive = false, value = 4),
            Rule(id = "8", name = "SameLineForIfBrace", isActive = false),
            Rule(id = "9", name = "SameLineForElseBrace", isActive = false),
            Rule(id = "10", name = "NewLineForIfBrace", isActive = false),
            Rule(id = "11", name = "SpaceAfterAndBeforeOperators", isActive = false),
            Rule(id = "12", name = "OnlyOneSpacePermitted", isActive = false),
            Rule(id = "13", name = "NewLineAfterBrace", isActive = false)
        )
    }
}
