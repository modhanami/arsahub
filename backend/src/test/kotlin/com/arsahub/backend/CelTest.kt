@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.arsahub.backend

import com.arsahub.backend.services.ruleengine.RuleEngine
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class CelTest {
    @ParameterizedTest(name = "{0}")
    @MethodSource("expressionProvider")
    fun testRun(
        name: String,
        expression: String,
        expected: Boolean,
    ) {
        val program = RuleEngine.getProgram(expression)

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

    companion object {
        @JvmStatic
        fun expressionProvider() =
            listOf(
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
                // list (used as set)
                //                [10,20,30].containsAll([20]) // true
// [10,20,30].containsAll([20, 10]) // true
// [10,20,30].containsAll([20, 40]) // false
                Arguments.of("list: containsAll", "[10, 20, 30].containsAll([20])", true),
                Arguments.of("list: containsAll", "[10, 20, 30].containsAll([20, 10])", true),
                Arguments.of("list: containsAll", "[10, 20, 30].containsAll([20, 40])", false),
                // TODO: negation
//                Arguments.of("string: not equals", "'Test' != 'est'", true),
//                Arguments.of("int: not equals", "27 != 28", true),
                // TODO: nullable
            )
    }
}
