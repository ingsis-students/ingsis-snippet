package com.students.ingsissnippet.config.producers

import com.students.ingsissnippet.config.SnippetMessage
import kotlinx.coroutines.reactive.awaitSingle
import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service


interface LinterRuleProducer {
    suspend fun publishEvent(snippetMessage: SnippetMessage)
}
@Service
class RedisLinterRuleProducer @Autowired constructor(
    @Value("\${stream.lint.key}") streamKey: String,
    redis: ReactiveRedisTemplate<String, String>
) : LinterRuleProducer, RedisStreamProducer(streamKey, redis) {
    override suspend fun publishEvent(snippetMessage: SnippetMessage) {
        emit(snippetMessage).awaitSingle()
    }
}

