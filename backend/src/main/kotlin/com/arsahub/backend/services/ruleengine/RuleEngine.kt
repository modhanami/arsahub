package com.arsahub.backend.services.ruleengine

import com.arsahub.backend.dtos.request.TriggerSendRequest
import com.arsahub.backend.models.App
import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.Rule
import com.arsahub.backend.models.RuleProgress
import com.arsahub.backend.models.RuleRepeatability
import com.arsahub.backend.models.Trigger
import com.arsahub.backend.models.TriggerField
import com.arsahub.backend.models.TriggerLog
import com.arsahub.backend.repositories.RuleProgressRepository
import com.arsahub.backend.repositories.TriggerLogRepository
import com.arsahub.backend.services.RuleService
import com.arsahub.backend.services.TriggerService
import com.arsahub.backend.services.actionhandlers.ActionHandlerRegistry
import com.arsahub.backend.services.actionhandlers.ActionResult
import dev.cel.checker.CelCheckerLegacyImpl
import dev.cel.common.CelFunctionDecl
import dev.cel.common.CelOptions
import dev.cel.common.CelOverloadDecl
import dev.cel.common.CelVarDecl
import dev.cel.common.types.CelType
import dev.cel.common.types.SimpleType
import dev.cel.compiler.CelCompiler
import dev.cel.compiler.CelCompilerImpl
import dev.cel.parser.CelParserImpl
import dev.cel.parser.Operator
import dev.cel.runtime.CelRuntime
import dev.cel.runtime.CelRuntimeLegacyImpl
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RuleEngine(
    private val actionHandlerRegistry: ActionHandlerRegistry,
    private val triggerLogRepository: TriggerLogRepository,
    private val ruleProgressRepository: RuleProgressRepository,
    private val triggerService: TriggerService,
    private val ruleService: RuleService,
) {
    private val logger = KotlinLogging.logger {}

    @Transactional
    fun trigger(
        app: App,
        appUser: AppUser,
        request: TriggerSendRequest,
        rawRequestJson: Map<String, Any>,
        afterAction: (ActionResult) -> Unit,
    ) {
        val trigger = triggerService.getTriggerOrThrow(request.key!!, app)

        logTrigger(trigger, app, appUser, rawRequestJson)

        triggerService.validateParamsAgainstTriggerFields(request.params, trigger.fields)

        val matchingRules = ruleService.getMatchingRules(app, trigger)
        val actionResults = processMatchingRules(matchingRules, app, appUser, request.params, afterAction)

        handleForwardChain(app, appUser, actionResults, afterAction)

        /*
         TODO: Currently, the trigger log is committed as a whole with other operations.
               Could possibly let Kafka handle this in the future and separate ingestion from processing.
         */
    }

    private fun processMatchingRules(
        matchingRules: List<Rule>,
        app: App,
        appUser: AppUser,
        params: Map<String, Any>?,
        afterAction: (ActionResult) -> Unit?,
    ): List<ActionResult> {
        val actionResults = mutableListOf<ActionResult>()

        for (rule in matchingRules) {
            logger.debug { "Checking rule ${rule.title} (${rule.id})" }

            if (
                // check repeatability
                !validateRepeatability(rule, appUser) ||
                // check the params against the rule conditions, if any
                !validateConditions(rule, appUser, params)
            ) {
                //  not repeatable or conditions don't match
                continue
            }

            // handle action
            val actionResult = activateRule(rule, app, appUser)
            actionResults.add(actionResult)

            afterAction(actionResult)
        }

        return actionResults
    }

    private fun handleForwardChain(
        app: App,
        appUser: AppUser,
        actionResults: List<ActionResult>,
        afterAction: (ActionResult) -> Unit?,
    ) {
        logger.info { "Handling forward chain" }
        val needToTriggerPointsReachedTrigger = actionResults.any { it is ActionResult.PointsUpdate }
        logger.debug { "Need to trigger points_reached trigger: $needToTriggerPointsReachedTrigger" }
        if (!needToTriggerPointsReachedTrigger) {
            return
        }

        val pointsReachedTrigger = triggerService.getBuiltInTriggerOrThrow("points_reached")
        val matchingRules = ruleService.getMatchingRules(app, pointsReachedTrigger)

        logger.debug { "Found ${matchingRules.size} matching rules for points_reached trigger" }
        processMatchingRules(matchingRules, app, appUser, emptyMap(), afterAction)
    }

    private fun logTrigger(
        trigger: Trigger,
        app: App,
        appUser: AppUser,
        rawRequestJson: Map<String, Any>,
    ): TriggerLog {
        val triggerLog =
            TriggerLog(
                trigger = trigger,
                requestBody = rawRequestJson.toMutableMap(),
                app = app,
                appUser = appUser,
            )
        triggerLogRepository.save(triggerLog)
        logger.debug {
            "Received trigger ${trigger.title} (${trigger.id}) for user ${appUser.userId} (${appUser.id}) " +
                "from app ${app.title} (${app.id})"
        }

        return triggerLog
    }

    private fun activateRule(
        rule: Rule,
        app: App,
        appUser: AppUser,
    ): ActionResult {
        val actionResult = actionHandlerRegistry.handleAction(rule, appUser)

        // update rule progress
        progressRule(rule, app, appUser)

        return actionResult
    }

    private fun progressRule(
        rule: Rule,
        app: App,
        appUser: AppUser,
    ): RuleProgress {
        val ruleProgress =
            ruleProgressRepository.findByRuleAndAppUser(rule, appUser) ?: RuleProgress(
                rule = rule,
                app = app,
                appUser = appUser,
            )
        ruleProgress.activationCount = (ruleProgress.activationCount ?: 0) + 1
        return ruleProgressRepository.save(ruleProgress)
    }

    private fun validateRepeatability(
        rule: Rule,
        appUser: AppUser,
    ): Boolean {
        if (rule.repeatability == RuleRepeatability.ONCE_PER_USER) {
            val ruleProgress = ruleProgressRepository.findByRuleAndAppUser(rule, appUser)
            if (ruleProgress != null && ruleProgress.activationCount!! > 0) {
                logger.debug { "Rule ${rule.title} (${rule.id}) has already been activated for user ${appUser.userId}" }
                return false
            }
        }
        return true
    }

//    private fun validateConditions(
//        rule: Rule,
//        appUser: AppUser,
//        params: Map<String, Any>?,
//    ): Boolean {
//        if (rule.conditions.isNullOrEmpty()) {
//            return params.isNullOrEmpty()
//        }
//
//        val conditions = rule.conditions!!
//        val conditionsMatch =
//            conditions.all { condition ->
//                val paramValue = params?.get(condition.key)
//                val conditionValue = condition.value
//
//                // TODO: forward-chain: This is a quick work around, utilizing conditions to check against appUser,
//                //  which is different from normal flow, that checks trigger params.
//                //  Ideally, we should have a separate trigger config field for this?
//                val isPointsReached = rule.trigger!!.key == "points_reached"
//
//                val matches =
//                    if (isPointsReached) {
//                        val pointsThreshold = conditionValue as? Int
//                        val appUserPoints = appUser.points
//                        logger.warn {
//                            "Workaround for points_reached: Checking points: " +
//                                "appUserPoints=$appUserPoints, pointsThreshold=$pointsThreshold"
//                        }
//                        pointsThreshold != null && appUserPoints != null && appUserPoints >= pointsThreshold
//                    } else {
//                        // TODO: support more operators
//                        paramValue == conditionValue
//                    }
//
//                if (matches) {
//                    logger.debug { "Condition ${condition.key} matches" }
//                } else {
//                    logger.debug { "Condition ${condition.key} does not match: $paramValue != $conditionValue" }
//                }
//
//                matches
//            }
//
//        logger.debug { "Rule ${rule.title} (${rule.id}) conditions match: $conditionsMatch" }
//
//        return conditionsMatch
//    }

    //    extension method on Trigger to get CEL type from a trigger field name
    private fun TriggerField.getCelType(): CelType {
        return when (type) {
            "integer" -> SimpleType.INT
            "text" -> SimpleType.STRING
            else -> throw IllegalArgumentException("Unsupported trigger field type: $type")
        }
    }

    private fun validateConditions(
        rule: Rule,
        appUser: AppUser,
        params: Map<String, Any>?,
    ): Boolean {
        // Refactor the above to use google/cel-java as the expression language for the conditions instead
        val conditionExpression = rule.conditionExpression
        if (conditionExpression.isNullOrEmpty()) {
            return params.isNullOrEmpty()
        }
        if (params.isNullOrEmpty()) {
            return false
        }

        // convert params to appropriate CEL types: CEL int = Java long, CEL string = Java string
        val programVariables =
            params.mapValues { (key, value) ->
                if (value is Int) {
                    value.toLong()
                } else {
                    value
                }
            }
        val varDecls =
            rule.trigger?.fields?.map { field ->
                val fieldName = field.key
                val fieldType = field.getCelType()
                CelVarDecl.newVarDeclaration(fieldName, fieldType)
            } ?: emptyList()

        val program = getProgram(conditionExpression, varDecls)

        val executionResult =
            program.eval(
                programVariables,
            ) as? Boolean
        return executionResult ?: false
    }

    private fun getProgram(
        expression: String,
        varDecls: List<CelVarDecl>,
    ): CelRuntime.Program {
        // Compile the expression into an Abstract Syntax Tree.
        val compiler = getCompiler(varDecls)
        val validationResult = compiler.compile(expression)
        if (validationResult.hasError()) {
            println(validationResult.issueString)
            throw IllegalArgumentException("Invalid expression: $expression")
        }
        val ast = validationResult.ast

        // Plan an executable program instance.
        val program = CEL_RUNTIME.createProgram(ast)
        return program
    }

    companion object {
        private val celOptions: CelOptions = CelOptions.current().build()

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

//        private val CEL_COMPILER: CelCompiler =
//            CelCompilerImpl.newBuilder(CelParserImpl.newBuilder(), CelCheckerLegacyImpl.newBuilder())
//                .setOptions(celOptions)
//                .setStandardEnvironmentEnabled(false)
//                .addFunctionDeclarations(celFunctionDecls)
//                .addVarDeclarations(
//                    listOf(
//                        CelVarDecl.newVarDeclaration(
//                            "trigger",
//                            SimpleType.DYN,
//                        ),
//                    ),
//                )
//                .build()

        private val CEL_RUNTIME: CelRuntime =
            CelRuntimeLegacyImpl.newBuilder()
                .setOptions(celOptions)
                .setStandardEnvironmentEnabled(false)
                .addFunctionBindings(celFunctionBindings)
                .build()
    }

    // Construct the compilation and runtime environments.
    // These instances are immutable and thus trivially thread-safe and amenable to caching.
    private fun getCompiler(varDecls: List<CelVarDecl>): CelCompiler {
        return CelCompilerImpl.newBuilder(CelParserImpl.newBuilder(), CelCheckerLegacyImpl.newBuilder())
            .setOptions(celOptions)
            .setStandardEnvironmentEnabled(false)
            .addFunctionDeclarations(celFunctionDecls)
            .addVarDeclarations(varDecls)
            .build()
    }
}
