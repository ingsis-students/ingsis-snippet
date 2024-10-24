package com.students.ingsissnippet.repositories

import com.students.ingsissnippet.entities.Language
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LanguageRepository : JpaRepository<Language, Long>
