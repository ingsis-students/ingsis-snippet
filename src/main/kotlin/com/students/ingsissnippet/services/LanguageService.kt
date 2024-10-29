package com.students.ingsissnippet.services

import com.students.ingsissnippet.entities.Language
import com.students.ingsissnippet.errors.LanguageNotFound
import com.students.ingsissnippet.repositories.LanguageRepository
import org.springframework.stereotype.Service

@Service
class LanguageService(
    private val languageRepository: LanguageRepository
) {
    fun getAll(): List<Language> {
        return languageRepository.findAll()
    }

    fun create(language: Language): Language {
        return languageRepository.save(language)
    }

    fun getLanguageById(id: Long?): Language {
        if (id == null) {
            throw LanguageNotFound("Language not found when trying to get it")
        }
        return languageRepository.findById(id)
            .orElseThrow { LanguageNotFound("Language not found when trying to get it") }
    }
}
