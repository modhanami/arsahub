package com.arsahub.backend.services

import dev.cel.checker.CelCheckerLegacyImpl
import dev.cel.common.CelOptions
import dev.cel.common.CelValidationResult
import dev.cel.common.CelVarDecl
import dev.cel.common.types.CelType
import dev.cel.common.types.SimpleType
import dev.cel.compiler.CelCompiler
import dev.cel.compiler.CelCompilerImpl
import dev.cel.parser.CelParserImpl
import dev.cel.runtime.CelRuntime
import dev.cel.runtime.CelRuntimeLegacyImpl
import io.github.oshai.kotlinlogging.KotlinLogging

class TemplateEngine {
    companion object {
        private val celOptions: CelOptions = CelOptions.current().build()
        private val logger = KotlinLogging.logger {}

        // Construct the compilation and runtime environments.
        // These instances are immutable and thus trivially thread-safe and amenable to caching.
        private val CEL_RUNTIME: CelRuntime =
            CelRuntimeLegacyImpl.newBuilder()
                .setOptions(celOptions)
                .setStandardEnvironmentEnabled(false)
                .build()

        fun getProgram(
            expression: String,
            varDecls: List<CelVarDecl> = emptyList(),
            resultType: CelType = SimpleType.ANY,
        ): CelRuntime.Program {
            val validationResult = getProgramValidationResult(expression, varDecls)
            if (validationResult.hasError()) {
                logger.error { "Invalid expression: $expression, errors: ${validationResult.issueString}" }
                throw IllegalArgumentException("Invalid expression: $expression")
            }
            val ast = validationResult.ast
            logger.trace { "Program AST: $ast" }

            // Plan an executable program instance.
            val program = CEL_RUNTIME.createProgram(ast)
            return program
        }

        fun getProgramValidationResult(
            expression: String,
            varDecls: List<CelVarDecl> = emptyList(),
            resultType: CelType = SimpleType.ANY,
        ): CelValidationResult {
            // Compile the expression into an Abstract Syntax Tree.
            val compiler = getCompiler(varDecls, resultType)
            return compiler.compile(expression)
        }

        private fun getCompiler(
            varDecls: List<CelVarDecl>,
            resultType: CelType,
        ): CelCompiler {
            return CelCompilerImpl.newBuilder(CelParserImpl.newBuilder(), CelCheckerLegacyImpl.newBuilder())
                .setOptions(celOptions)
                .setStandardEnvironmentEnabled(false)
                .addVarDeclarations(varDecls)
                .setResultType(resultType)
                .build()
        }
    }
}
