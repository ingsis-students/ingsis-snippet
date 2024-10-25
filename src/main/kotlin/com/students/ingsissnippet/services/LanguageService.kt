package com.students.ingsissnippet.services

import com.students.ingsissnippet.entities.Language
import com.students.ingsissnippet.repositories.LanguageRepository
import org.springframework.stereotype.Service

@Service
class LanguageService(
    private val languageRepository: LanguageRepository
) {
    fun getAll(): List<Language> {
        return languageRepository.findAll()
    }

    fun get(id: String): Language {
        return languageRepository.findById(id.toLong()).orElseThrow()
    }

    fun create(language: Language): Language {
        return languageRepository.save(language)
    }
}
