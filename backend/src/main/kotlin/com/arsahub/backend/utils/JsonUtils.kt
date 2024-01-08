package com.arsahub.backend.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SchemaValidatorsConfig
import com.networknt.schema.SpecVersionDetector
import org.springframework.stereotype.Component
import java.net.URI

val defaultSchemaValidatorsConfig: SchemaValidatorsConfig =
    SchemaValidatorsConfig().apply {
        isTypeLoose = true
    }

@Component
class JsonUtils(
    private val objectMapper: ObjectMapper,
) {
    private val schemaValidatorsConfig: SchemaValidatorsConfig = defaultSchemaValidatorsConfig

    fun validate(
        jsonSchema: JsonNode,
        jsonNode: JsonNode,
    ): JsonSchemaValidationResult {
        val factory = JsonSchemaFactory.getInstance(SpecVersionDetector.detect(jsonSchema))

        val schema = factory.getSchema(jsonSchema, schemaValidatorsConfig)
        val errors = schema.validate(jsonNode)

        return JsonSchemaValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
        )
    }

    fun validate(
        jsonSchema: MutableMap<String, Any>,
        json: Map<String, Any>,
    ): JsonSchemaValidationResult {
        val jsonSchemaJsonNode = objectMapper.valueToTree<JsonNode>(jsonSchema)
        val jsonNode = objectMapper.valueToTree<JsonNode>(json)
        return validate(jsonSchemaJsonNode, jsonNode)
    }

    fun convertJsonStringToMutableMap(json: String): MutableMap<String, Any> {
        return objectMapper.convertValue(
            objectMapper.readTree(json),
            object : TypeReference<MutableMap<String, Any>>() {},
        )
    }

    fun validateAgainstMetaSchema(jsonSchema: Map<String, Any>): Boolean {
        val jsonSchemaJsonNode = objectMapper.valueToTree<JsonNode>(jsonSchema)
        val versionFlag = SpecVersionDetector.detect(jsonSchemaJsonNode)
        val factory = JsonSchemaFactory.getInstance(versionFlag)
        val metaSchema = factory.getSchema(URI(versionFlag.id))
        val errors = metaSchema.validate(jsonSchemaJsonNode)
        return errors.isEmpty()
    }
}
