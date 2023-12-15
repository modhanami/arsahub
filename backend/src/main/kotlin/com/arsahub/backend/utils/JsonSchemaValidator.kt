package com.arsahub.backend.utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SchemaValidatorsConfig
import com.networknt.schema.SpecVersionDetector
import org.springframework.stereotype.Component

val defaultSchemaValidatorsConfig: SchemaValidatorsConfig = SchemaValidatorsConfig().apply {
    isTypeLoose = true
}

@Component
class JsonSchemaValidator(
    private val objectMapper: ObjectMapper = ObjectMapper(),
    private val schemaValidatorsConfig: SchemaValidatorsConfig = defaultSchemaValidatorsConfig
) {

    fun validate(jsonSchema: JsonNode, jsonNode: JsonNode): JsonSchemaValidationResult {
        val factory = JsonSchemaFactory.getInstance(SpecVersionDetector.detect(jsonSchema))

        val schema = factory.getSchema(jsonSchema, schemaValidatorsConfig)
        val errors = schema.validate(jsonNode)

        return JsonSchemaValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }

    fun validate(jsonSchema: MutableMap<String, Any>, json: Map<String, String>): JsonSchemaValidationResult {
        val jsonSchemaJsonNode = objectMapper.valueToTree<JsonNode>(jsonSchema)
        val jsonNode = objectMapper.valueToTree<JsonNode>(json)
        return validate(jsonSchemaJsonNode, jsonNode)
    }

    fun convertJsonStringToMap(json: String): Map<String, Any> {
        val typeRef = object : com.fasterxml.jackson.core.type.TypeReference<Map<String, Any>>() {}
        return objectMapper.readValue(json, typeRef)
    }
}

