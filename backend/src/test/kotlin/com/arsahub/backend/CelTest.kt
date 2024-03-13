@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.arsahub.backend

import dev.cel.checker.CelCheckerLegacyImpl
import dev.cel.common.CelFunctionDecl
import dev.cel.common.CelOptions
import dev.cel.common.CelOverloadDecl
import dev.cel.common.CelVarDecl
import dev.cel.common.types.SimpleType
import dev.cel.compiler.CelCompiler
import dev.cel.compiler.CelCompilerImpl
import dev.cel.parser.CelParserImpl
import dev.cel.parser.Operator
import dev.cel.runtime.CelRuntime
import dev.cel.runtime.CelRuntimeLegacyImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class CelTest {
    // Types + Operators Supported
    // - Text -> exact match, contains, starts with, ends with
    // - Integer -> == != < > <= >=

    @ParameterizedTest(name = "{0}")
    @MethodSource("expressionProvider")
    fun testRun(
        name: String,
        expression: String,
        expected: Boolean,
    ) {
        val program = getProgram(expression)

        // Evaluate the program with an input variable.
        try {
            val rawResult = program.eval()
            println(
                """
            |Expression: $expression
            |Expected: $expected
            |Result: $rawResult
                """.trimMargin(),
            )
            val result = rawResult as Boolean
            Assertions.assertEquals(expected, result)
        } catch (e: Exception) {
            println(
                """
            |Expression: $expression
            |Expected: $expected
            |Result: $e
                """.trimMargin(),
            )
            Assertions.fail(e.message)
        }
    }

    @Test
    fun testEvaluatingTriggerParamsAgainstTriggerFieldsDefinitionWithFields() {
//            fun validateParamsAgainstTriggerFields(
//        conditions: Map<String, Any>?,
//        fields: Iterable<TriggerField>,
//    ) {
//        for (conditionKey in conditions?.keys ?: emptyList()) {
//            // ensure the key and it's value are not empty
//            require(conditionKey.isNotBlank()) { "Condition key cannot be empty" }
//            val conditionValue = conditions?.get(conditionKey)
//            require(conditionValue != null && conditionValue.toString().isNotBlank()) {
//                "Condition value cannot be empty"
//            }
//
//            val targetField = fields.find { it.key == conditionKey } ?: continue
//            val targetFieldType = TriggerFieldType.fromString(targetField.type!!)
//
//            requireNotNull(targetFieldType) {
//                val message = "Field ${targetField.key} has an invalid type: ${targetField.type}"
//                logger.error { message }
//                message
//            }
//
//            when (targetFieldType) {
//                TriggerFieldType.INTEGER ->
//                    require(
//                        conditionValue is Int,
//                    ) { "Field ${targetField.key} is not an integer, got $conditionValue" }
//
//                TriggerFieldType.TEXT ->
//                    require(
//                        conditionValue is String,
//                    ) { "Field ${targetField.key} is not a text, got $conditionValue" }
//            }
//        }
//    }

        val triggerParams =
            mapOf(
                "name" to "Jing",
                "age" to 27L,
            )

        val program = getProgram("trigger.name == 'Jing' && trigger.age == 27")
//        val program = getProgram("trigger.age == 27")
//        val program = getProgram("trigger.name == 'Jing'")

        val rawResult =
            program.eval(
                mapOf(
                    "trigger" to triggerParams,
                ),
            )
        val result = rawResult as Boolean
        println(result)
    }

    private fun getProgram(expression: String): CelRuntime.Program {
        // Compile the expression into an Abstract Syntax Tree.
        val validationResult = CEL_COMPILER.compile(expression)
        if (validationResult.hasError()) {
            println(validationResult.issueString)
            return Assertions.fail(validationResult.issueString)
        }
        val ast = validationResult.ast

        // Plan an executable program instance.
        val program = CEL_RUNTIME.createProgram(ast)
        return program
    }

    companion object {
        val celOptions = CelOptions.current().build()

        private const val OPERATOR_CONTAINS = "contains"

        private val celFunctionDecls =
            listOf<CelFunctionDecl>(
                CelFunctionDecl.newFunctionDeclaration(
                    Operator.EQUALS.function,
                    CelOverloadDecl.newGlobalOverload(
                        "equals_string",
                        "equality",
                        SimpleType.BOOL,
                        SimpleType.STRING,
                        SimpleType.STRING,
                    ),
                    CelOverloadDecl.newGlobalOverload(
                        "equals_int",
                        "equality",
                        SimpleType.BOOL,
                        SimpleType.INT,
                        SimpleType.INT,
                    ),
                ),
                CelFunctionDecl.newFunctionDeclaration(
                    Operator.LESS.function,
                    CelOverloadDecl.newGlobalOverload(
                        "less_int",
                        "ordering",
                        SimpleType.BOOL,
                        SimpleType.INT,
                        SimpleType.INT,
                    ),
                ),
                CelFunctionDecl.newFunctionDeclaration(
                    Operator.LESS_EQUALS.function,
                    CelOverloadDecl.newGlobalOverload(
                        "less_equals_int",
                        "ordering",
                        SimpleType.BOOL,
                        SimpleType.INT,
                        SimpleType.INT,
                    ),
                ),
                CelFunctionDecl.newFunctionDeclaration(
                    Operator.GREATER.function,
                    CelOverloadDecl.newGlobalOverload(
                        "greater_int",
                        "ordering",
                        SimpleType.BOOL,
                        SimpleType.INT,
                        SimpleType.INT,
                    ),
                ),
                CelFunctionDecl.newFunctionDeclaration(
                    Operator.GREATER_EQUALS.function,
                    CelOverloadDecl.newGlobalOverload(
                        "greater_equals_int",
                        "ordering",
                        SimpleType.BOOL,
                        SimpleType.INT,
                        SimpleType.INT,
                    ),
                ),
                CelFunctionDecl.newFunctionDeclaration(
                    OPERATOR_CONTAINS,
                    CelOverloadDecl.newMemberOverload(
                        "contains_string",
                        "tests whether the string operand contains the substring",
                        SimpleType.BOOL,
                        SimpleType.STRING,
                        SimpleType.STRING,
                    ),
                ),
                CelFunctionDecl.newFunctionDeclaration(
                    "endsWith",
                    CelOverloadDecl.newMemberOverload(
                        "ends_with_string",
                        "tests whether the string operand ends with the suffix argument",
                        SimpleType.BOOL,
                        SimpleType.STRING,
                        SimpleType.STRING,
                    ),
                ),
                CelFunctionDecl.newFunctionDeclaration(
                    "startsWith",
                    CelOverloadDecl.newMemberOverload(
                        "starts_with_string",
                        "tests whether the string operand starts with the prefix argument",
                        SimpleType.BOOL,
                        SimpleType.STRING,
                        SimpleType.STRING,
                    ),
                ),
                CelFunctionDecl.newFunctionDeclaration(
                    Operator.LOGICAL_AND.function,
                    CelOverloadDecl.newGlobalOverload(
                        "logical_and",
                        "logical_and",
                        SimpleType.BOOL,
                        SimpleType.BOOL,
                        SimpleType.BOOL,
                    ),
                ),
                CelFunctionDecl.newFunctionDeclaration(
                    Operator.LOGICAL_OR.function,
                    CelOverloadDecl.newGlobalOverload(
                        "logical_or",
                        "logical or",
                        SimpleType.BOOL,
                        SimpleType.BOOL,
                        SimpleType.BOOL,
                    ),
                ),
            )
        private val celFunctionBindings =
            listOf(
                CelRuntime.CelFunctionBinding.from(
                    "equals_string",
                    String::class.javaObjectType,
                    String::class.javaObjectType,
                    String::equals,
                ),
                CelRuntime.CelFunctionBinding.from(
                    "equals_int",
                    Long::class.javaObjectType,
                    Long::class.javaObjectType,
                ) { x, y -> x == y },
                CelRuntime.CelFunctionBinding.from(
                    "less_int",
                    Long::class.javaObjectType,
                    Long::class.javaObjectType,
                ) { x, y -> x < y },
                CelRuntime.CelFunctionBinding.from(
                    "less_equals_int",
                    Long::class.javaObjectType,
                    Long::class.javaObjectType,
                ) { x, y -> x <= y },
                CelRuntime.CelFunctionBinding.from(
                    "greater_int",
                    Long::class.javaObjectType,
                    Long::class.javaObjectType,
                ) { x, y -> x > y },
                CelRuntime.CelFunctionBinding.from(
                    "greater_equals_int",
                    Long::class.javaObjectType,
                    Long::class.javaObjectType,
                ) { x, y -> x >= y },
                CelRuntime.CelFunctionBinding.from(
                    "contains_string",
                    String::class.javaObjectType,
                    String::class.javaObjectType,
                    String::contains,
                ),
                CelRuntime.CelFunctionBinding.from(
                    "ends_with_string",
                    String::class.javaObjectType,
                    String::class.javaObjectType,
                    String::endsWith,
                ),
                CelRuntime.CelFunctionBinding.from(
                    "starts_with_string",
                    String::class.javaObjectType,
                    String::class.javaObjectType,
                    String::startsWith,
                ),
            )

        // Construct the compilation and runtime environments.
        // These instances are immutable and thus trivially thread-safe and amenable to caching.
        private val CEL_COMPILER: CelCompiler =
            CelCompilerImpl.newBuilder(CelParserImpl.newBuilder(), CelCheckerLegacyImpl.newBuilder())
                .setOptions(celOptions)
                .setStandardEnvironmentEnabled(false)
                .addFunctionDeclarations(celFunctionDecls)
//                .addVar(
//                    "trigger",
//                    StructType.create(
//                        "trigger",
//                        ImmutableSet.of(
//                            "name",
//                            "age",
//                        ),
//                    ) { fieldName ->
//                        when (fieldName) {
//                            "name" -> SimpleType.STRING
//                            "age" -> SimpleType.INT
//                            else -> SimpleType.NULL_TYPE
//                        }.let { Optional.of(it) }
//                    },
//                )
                .addVarDeclarations(
                    listOf(
                        CelVarDecl.newVarDeclaration(
                            "trigger",
                            SimpleType.DYN,
                        ),
                    ),
                )
                .build()

        private val CEL_RUNTIME: CelRuntime =
            CelRuntimeLegacyImpl.newBuilder()
                .setOptions(celOptions)
                .setStandardEnvironmentEnabled(false)
                .addFunctionBindings(celFunctionBindings)
                .build()

        // parameterized test using JUnit 5
        @JvmStatic
        fun expressionProvider() =
            listOf(
                // name, expression, expected boolean result
                // exact match
//                Arguments.of("string: exact match", "'Jing Jing' == 'Jing Jing'", true),
//                Arguments.of("string: exact match", "'Jing Jing' == 'jing jing'", false),
//                Arguments.of("string: exact match", "'Jing Jing' == 'Jing'", false),
//                // contains
//                Arguments.of("string: contains", "'Jing Jing'.contains('Jing')", true),
//                Arguments.of("string: contains", "'Jing Jing'.contains('Jing Jing')", true),
//                Arguments.of("string: contains", "'Jing Jing'.contains('Jing Jing Jing')", false),
//                Arguments.of("string: contains", "'Jing Jing'.contains('jing')", false),
//                // starts with
//                Arguments.of("string: starts with", "'Jing Jing'.startsWith('Jing')", true),
//                Arguments.of("string: starts with", "'Jing Jing'.startsWith('Jing Jing')", true),
//                Arguments.of("string: starts with", "'Jing Jing'.startsWith('Jing Jing Jing')", false),
//                Arguments.of("string: starts with", "'Jing Jing'.startsWith('jing')", false),
//                // ends with
//                Arguments.of("string: ends with", "'Jing Jing'.endsWith('Jing')", false),
//                Arguments.of("string: ends with", "'Jing Jing'.endsWith('Jing Jing')", true),
//                Arguments.of("string: ends with", "'Jing Jing'.endsWith('Jing Jing Jing')", false),
//                Arguments.of("string: ends with", "'Jing Jing'.endsWith('jing')", false),
                // only need to verify the expression works for each operator,
                // as the operators are tested in the standard library
                Arguments.of("string: equals", "'Test' == 'Test'", true),
                Arguments.of("string: contains", "'Test'.contains('es')", true),
                Arguments.of("string: ends_with", "'Test'.endsWith('st')", true),
                Arguments.of("string: starts_with", "'Test'.startsWith('Te')", true),
                Arguments.of("int: equals", "27 == 27", true),
                Arguments.of("int: greater", "27 > 26", true),
                Arguments.of("int: greater_equals", "27 >= 27", true),
                Arguments.of("int: less", "27 < 28", true),
                Arguments.of("int: less_equals", "27 <= 27", true),
                Arguments.of("logical_and", "27 <= 27 && 'Test'.startsWith('Te')", true),
                Arguments.of("logical_or", "26 > 27 || 'Test'.startsWith('Te')", true),
                Arguments.of("logical_group", "(26 > 27 || 25 > 0) || 'Test'.startsWith('Te')", true),
                // TODO: negation
//                Arguments.of("string: not equals", "'Test' != 'est'", true),
//                Arguments.of("int: not equals", "27 != 28", true),
                // TODO: nullable
            )
    }
}
