package com.arsahub.backend.dtos.request

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpOutputMessage
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter

object ActionKeys {
    const val ADD_POINTS = "add_points"
    const val UNLOCK_ACHIEVEMENT = "unlock_achievement"
}

sealed class ActionDefinition(
    val key: String
)

data class AddPointsAction(
    val points: Int,
) : ActionDefinition(ActionKeys.ADD_POINTS)

data class UnlockAchievementAction(
    val achievementId: Long,
) : ActionDefinition(
    ActionKeys.UNLOCK_ACHIEVEMENT
)


class ActionDefinitionMessageConverter : HttpMessageConverter<ActionDefinition> {

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    init {
        val module = SimpleModule().apply {
            addDeserializer(ActionDefinition::class.java, ActionDefinitionDeserializer())
        }
        objectMapper.registerModule(module)
    }

    override fun canRead(clazz: Class<*>, mediaType: MediaType?): Boolean = clazz == ActionDefinition::class.java

    override fun canWrite(clazz: Class<*>, mediaType: MediaType?): Boolean = false

    override fun getSupportedMediaTypes(): MutableList<MediaType> = mutableListOf(MediaType.APPLICATION_JSON)

    override fun read(clazz: Class<out ActionDefinition>, inputMessage: HttpInputMessage): ActionDefinition {
        return objectMapper.readValue(inputMessage.body, ActionDefinition::class.java)
    }

    override fun write(t: ActionDefinition, contentType: MediaType?, outputMessage: HttpOutputMessage) {
        // not implemented because we're only concerned with reading the request
    }
}

class ActionDefinitionDeserializer : JsonDeserializer<ActionDefinition>() {
    override fun deserialize(jsonParser: JsonParser, ctxt: DeserializationContext): ActionDefinition {
        val node: JsonNode = jsonParser.codec.readTree(jsonParser)
        val key: String = node.get("key").textValue()
        val params = node.get("params")
        return when (key) {
            "add_points" -> {
                val rawPoints = params.get("points")
                if (!rawPoints.canConvertToInt()) {
                    throw IllegalArgumentException("Points is invalid")
                }
                AddPointsAction(rawPoints.asInt())
            }

            "unlock_achievement" -> {
                val rawAchievementId = params.get("achievementId")
                if (!rawAchievementId.canConvertToLong()) {
                    throw IllegalArgumentException("Achievement ID is invalid")
                }
                UnlockAchievementAction(rawAchievementId.asLong())
            }

            else -> throw IllegalArgumentException("Unknown action key: $key")
        }
    }
}

@Bean
fun actionDefinitionMessageConverter(): ActionDefinitionMessageConverter {
    return ActionDefinitionMessageConverter()
}