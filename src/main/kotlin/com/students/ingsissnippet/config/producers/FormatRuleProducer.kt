package com.students.ingsissnippet.config.producers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.students.ingsissnippet.config.SnippetMessage
import kotlinx.coroutines.reactive.awaitSingle
import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.aot.generate.Generated
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service

interface FormatRuleProducer {
    suspend fun publishEvent(snippetMessage: SnippetMessage)
}
@Service
@Profile("!test")
@Generated
class RedisFormatRuleProducer @Autowired constructor(
    @Value("\${stream.format.key}") streamKey: String,
    redis: ReactiveRedisTemplate<String, String>
) : FormatRuleProducer, RedisStreamProducer(streamKey, redis) {
    override suspend fun publishEvent(snippetMessage: SnippetMessage) {
        println("publicando el evento")
        val messageJson = jacksonObjectMapper().writeValueAsString(snippetMessage)
        println("mensaje del evento a publicar: $messageJson")
        emit(messageJson).awaitSingle() // serialized the snippetMessage
        println("evento publicado")
    }
}
