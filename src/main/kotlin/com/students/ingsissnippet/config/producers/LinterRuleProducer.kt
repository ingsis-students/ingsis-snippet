package com.students.ingsissnippet.config.producers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.students.ingsissnippet.config.SnippetMessage
import kotlinx.coroutines.reactive.awaitSingle
import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service

interface LinterRuleProducer {
    suspend fun publishEvent(snippetMessage: SnippetMessage)
}
@Service
@Profile("!test")
class RedisLinterRuleProducer @Autowired constructor(
    @Value("\${stream.lint.key}") streamKey: String,
    redis: ReactiveRedisTemplate<String, String>
) : LinterRuleProducer, RedisStreamProducer(streamKey, redis) {
    override suspend fun publishEvent(snippetMessage: SnippetMessage) {
        val messageJson = jacksonObjectMapper().writeValueAsString(snippetMessage)
        emit(messageJson).awaitSingle() // serialized the snippetMessage
    }
}
