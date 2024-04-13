package com.arsahub.backend.services.ruleengine

import com.arsahub.backend.dtos.request.TriggerSendRequest
import com.arsahub.backend.models.App
import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.Rule
import com.arsahub.backend.models.RuleProgress
import com.arsahub.backend.models.RuleRepeatability
import com.arsahub.backend.models.RuleTriggerFieldState
import com.arsahub.backend.models.Trigger
import com.arsahub.backend.models.TriggerField
import com.arsahub.backend.models.TriggerFieldType
import com.arsahub.backend.models.TriggerLog
import com.arsahub.backend.repositories.RuleProgressRepository
import com.arsahub.backend.repositories.RuleTriggerFieldStateRepository
import com.arsahub.backend.repositories.TriggerLogRepository
import com.arsahub.backend.services.RuleService
import com.arsahub.backend.services.TriggerService
import com.arsahub.backend.services.actionhandlers.ActionHandlerRegistry
import com.arsahub.backend.services.actionhandlers.ActionResult
import com.arsahub.backend.services.getAccumulatableFields
import dev.cel.checker.CelCheckerLegacyImpl
import dev.cel.common.CelFunctionDecl
import dev.cel.common.CelOptions
import dev.cel.common.CelOverloadDecl
import dev.cel.common.CelValidationResult
import dev.cel.common.CelVarDecl
import dev.cel.common.types.CelType
import dev.cel.common.types.ListType
import dev.cel.common.types.SimpleType
import dev.cel.common.types.TypeParamType
import dev.cel.compiler.CelCompiler
import dev.cel.compiler.CelCompilerImpl
import dev.cel.parser.CelParserImpl
import dev.cel.parser.Operator
import dev.cel.runtime.CelRuntime
import dev.cel.runtime.CelRuntimeLegacyImpl
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

fun Iterable<TriggerField>.getCelVarDecls(): List<CelVarDecl> {
    return map { field ->
        val fieldName = field.key
        val fieldType = field.getCelType()
        CelVarDecl.newVarDeclaration(fieldName, fieldType)
    }
}

fun TriggerField.getCelType(): CelType {
    return when (TriggerFieldType.fromString(this.type!!)) {
        TriggerFieldType.INTEGER -> SimpleType.INT
        TriggerFieldType.TEXT -> SimpleType.STRING
        TriggerFieldType.INTEGER_SET -> ListType.create(SimpleType.INT)
        TriggerFieldType.TEXT_SET -> ListType.create(SimpleType.STRING)
        else -> throw IllegalArgumentException("Unsupported trigger field type: $type")
    }
}

@Service
class RuleEngine(
    private val actionHandlerRegistry: ActionHandlerRegistry,
    private val triggerLogRepository: TriggerLogRepository,
    private val ruleProgressRepository: RuleProgressRepository,
    private val ruleTriggerFieldStateRepository: RuleTriggerFieldStateRepository,
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
        afterAction: (ActionResult, Rule) -> Unit?,
    ) {
        logger.info { "Triggering" }
        val trigger = triggerService.getTriggerOrThrow(request.key!!, app)

        logTrigger(trigger, app, appUser, rawRequestJson)

        triggerService.validateParamsAgainstTriggerFields(request.params, trigger.fields)

        val referencingRules = ruleService.getRulesByReferencedTrigger(app, trigger)
        val matchingRules =
            getMatchingRules(referencingRules, app, appUser, request.params) { rule, params ->
//                val updatedRuleTriggerFieldStates =
//                    updateRuleTriggerFieldStateForRule(app, appUser, rule, trigger, params)
//                if (updatedRuleTriggerFieldStates.isEmpty()) {
//                    logger.debug { "No updated rule trigger field states" }
//                } else {
//                    logger.debug { "Updated rule trigger field states: $updatedRuleTriggerFieldStates" }
//                }
//
//                val accumulatedFields = getAccumulatedFields(rule, trigger)
//                val paramsWithAccumulatedFields =
//                    params?.toMutableMap()?.apply {
//                        accumulatedFields.forEach { field ->
//                            val triggerField = trigger.fields.first { it.key == field }
//                            val state =
//                                ruleTriggerFieldStateRepository.findByAppAndAppUserAndRuleAndTriggerField(
//                                    app,
//                                    appUser,
//                                    rule,
//                                    triggerField,
//                                )
//                                    ?: return@forEach
//                            val value =
//                                when (TriggerFieldType.fromString(triggerField.type!!)) {
//                                    TriggerFieldType.INTEGER_SET -> state.stateIntSet!!.toList()
//                                    else -> throw IllegalArgumentException("Unsupported trigger field type: ${triggerField.type}")
//                                }
//                            logger.debug { "Setting param $field to $value, was ${if (containsKey(field)) get(field) else null}" }
//                            put(triggerField.key!!, value)
//                        }
//                    }
//                return@getMatchingRules paramsWithAccumulatedFields

                params
            }
        val actionResults = processMatchingRules(matchingRules, app, appUser, request.params, afterAction)

        val needToTriggerPointsReachedTrigger = actionResults.any { it is ActionResult.PointsUpdate }
        if (needToTriggerPointsReachedTrigger) {
            logger.debug { "Handling forward chain" }
            handleForwardChain(app, appUser, afterAction)
        } else {
            logger.debug { "No need to trigger points_reached trigger" }
        }

        /*
         TODO: Currently, the trigger log is committed as a whole with other operations.
               Could possibly let Kafka handle this in the future and separate ingestion from processing.
         */
    }

    fun dryTrigger(
        app: App,
        appUser: AppUser,
        request: TriggerSendRequest,
    ): List<Rule> {
        logger.info { "Dry triggering" }
        val trigger = triggerService.getTriggerOrThrow(request.key!!, app)

        triggerService.validateParamsAgainstTriggerFields(request.params, trigger.fields)

        val referencingRules = ruleService.getRulesByReferencedTrigger(app, trigger)
        val matchingRules = getMatchingRules(referencingRules, app, appUser, request.params)

        return matchingRules

        // TODO: dry trigger should get complete flow, including forward chain?
    }

    private fun getMatchingRules(
        rules: List<Rule>,
        app: App,
        appUser: AppUser,
        params: Map<String, Any>?,
        preprocessParams: (
            (
                rule: Rule,
                params: Map<String, Any>?,
            ) -> Map<String, Any>?
        )? = null,
    ): List<Rule> {
        return rules.filter { rule ->
            logger.debug { "Checking rule ${rule.title} (${rule.id})" }

            val preprocessedParams = preprocessParams?.let { it(rule, params) } ?: params

            return@filter (
                // check repeatability
                validateRepeatability(rule, app, appUser) &&
                    // check the condition expression, if any
                    validateConditionExpression(rule, preprocessedParams, appUser)
            )
        }
    }

    private fun processMatchingRules(
        matchingRules: List<Rule>,
        app: App,
        appUser: AppUser,
        params: Map<String, Any>?,
        afterAction: (ActionResult, Rule) -> Unit?,
    ): List<ActionResult> {
        val actionResults = mutableListOf<ActionResult>()
        logger.debug { "Found ${matchingRules.size} matching rules" }

        for (rule in matchingRules) {
            logger.debug { "Checking rule ${rule.title} (${rule.id})" }

            // handle action
            val actionResult = activateRule(rule, app, appUser, params)
            actionResults.add(actionResult)

            afterAction(actionResult, rule)
        }

        return actionResults
    }

    private fun getAccumulatedFields(
        rule: Rule,
        trigger: Trigger,
    ): List<String> {
        return trigger.fields.getAccumulatableFields().map { it.key!! }.filter {
            rule.accumulatedFields?.contains(it) == true
        }
    }

    private fun updateRuleTriggerFieldStateForRule(
        app: App,
        appUser: AppUser,
        rule: Rule,
        trigger: Trigger,
        params: Map<String, Any>?,
    ): List<RuleTriggerFieldState> {
        val accumulatedFields = getAccumulatedFields(rule, trigger)

        logger.debug { "Accumulated fields: $accumulatedFields" }
        if (accumulatedFields.isEmpty()) {
            return emptyList()
        }

        return accumulatedFields.map { accumulatedField ->
            val value = params?.get(accumulatedField)
            if (value == null) {
                logger.debug { "No value for accumulated field $accumulatedField, skipping" }
                return@map null
            }

            val triggerField = trigger.fields.first { it.key == accumulatedField }
            val triggerFieldType = TriggerFieldType.fromString(triggerField.type!!)

            logger.debug { "Updating accumulated field $accumulatedField" }
            val state =
                ruleTriggerFieldStateRepository.findByAppAndAppUserAndRuleAndTriggerField(
                    app,
                    appUser,
                    rule,
                    triggerField,
                )
                    ?: RuleTriggerFieldState(
                        app = app,
                        appUser = appUser,
                        rule = rule,
                        triggerField = triggerField,
                    )

            when (triggerFieldType) {
                TriggerFieldType.INTEGER_SET -> {
                    if (!(value is List<*> && value.all { it is Int })) {
                        logger.error { "Value for accumulated field $accumulatedField is not a list of integers" }
                        return@map null
                    }

                    if (state.stateIntSet == null) {
                        state.stateIntSet = intArrayOf()
                    }

                    val prevState = state.stateIntSet!!
                    val newState =
                        prevState
                            .toMutableSet()
                            .apply { addAll(value.filterIsInstance<Int>()) }
                            .toIntArray()

                    state.stateIntSet = newState
                    logger.debug {
                        "${TriggerFieldType.INTEGER_SET}: value: $value, " +
                            "prevState: ${prevState.toList()}, newState: ${newState.toList()}"
                    }
                    return@map ruleTriggerFieldStateRepository.save(state)
                }

                else -> {
                    logger.error { "Unsupported trigger field type: $triggerFieldType" }
                    return@map null
                }
            }
        }.filterNotNull()
    }

    @Transactional
    fun handleForwardChain(
        app: App,
        appUser: AppUser,
        afterAction: (ActionResult, Rule) -> Unit? = { _, _ -> },
    ) {
        val pointsReachedTrigger = triggerService.getBuiltInTriggerOrThrow("points_reached")
        val referencingRules = ruleService.getRulesByReferencedTrigger(app, pointsReachedTrigger)
        val matchingRules = getMatchingRules(referencingRules, app, appUser, emptyMap())

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

    fun activateRule(
        rule: Rule,
        app: App,
        appUser: AppUser,
        params: Map<String, Any>?,
    ): ActionResult {
        val actionResult = actionHandlerRegistry.handleAction(rule, appUser, params)

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
            ruleProgressRepository.findByRuleAndAppAndAppUser(rule, app, appUser) ?: RuleProgress(
                rule = rule,
                app = app,
                appUser = appUser,
            )
        ruleProgress.activationCount = (ruleProgress.activationCount ?: 0) + 1
        return ruleProgressRepository.save(ruleProgress)
    }

    private fun validateRepeatability(
        rule: Rule,
        app: App,
        appUser: AppUser,
    ): Boolean {
        logger.debug { "Checking repeatability" }
        if (rule.repeatability == RuleRepeatability.ONCE_PER_USER) {
            val ruleProgress = ruleProgressRepository.findByRuleAndAppAndAppUser(rule, app, appUser)
            if (ruleProgress != null && ruleProgress.activationCount!! > 0) {
                logger.debug { "Rule ${rule.title} (${rule.id}) has already been activated for user ${appUser.userId}" }
                return false
            }
        }
        logger.debug { "Rule ${rule.title} (${rule.id}) is repeatable" }
        return true
    }

    private fun validateConditionExpression(
        rule: Rule,
        params: Map<String, Any>?,
        appUser: AppUser,
    ): Boolean {
        logger.debug { "Checking condition expression" }
        val conditionExpression = rule.conditionExpression
        if (conditionExpression.isNullOrEmpty()) {
            logger.debug { "No condition expression, params=$params" }
            return true
        }

        val isPointsReachedTrigger = rule.trigger?.key == "points_reached"
        if (isPointsReachedTrigger) {
            logger.info { "Workaround for points_reached: Checking points" }
            // TODO: fix this hacky regex
            val pointsThreshold = rule.conditionExpression?.let { "\\d+".toRegex().find(it)?.value?.toInt() }
            logger.info { "Checking points: appUserPoints=${appUser.points}, pointsThreshold=$pointsThreshold" }
            val appUserPoints = appUser.points
            return pointsThreshold != null && appUserPoints != null && appUserPoints >= pointsThreshold
        }

        if (params.isNullOrEmpty()) {
            logger.debug { "No params, skipping" }
            return false
        }

        // convert params to appropriate CEL types: CEL int = Java long, CEL string = Java string
        val programVariables =
            params.mapValues { (key, value) ->
                if (value is Int) {
                    value.toLong()
                } else if (value is List<*> && value.all { it is Int }) {
                    value.map { (it as Int).toLong() }
                } else {
                    value
                }
            }
        val varDecls =
            rule.trigger?.fields?.getCelVarDecls() ?: emptyList()

        logger.debug { "Program variables: $programVariables" }
        logger.debug { "Var decls: $varDecls" }

        val program = getProgram(conditionExpression, varDecls)

        val executionResult =
            program.eval(
                programVariables,
            ) as? Boolean
        return executionResult ?: false
    }

    companion object {
        private val celOptions: CelOptions = CelOptions.current().build()
        private val logger = KotlinLogging.logger {}

        private const val OPERATOR_CONTAINS = "contains"

        private val typeParamA: TypeParamType = TypeParamType.create("A")
        private val listOfA: ListType = ListType.create(typeParamA)

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
                CelFunctionDecl.newFunctionDeclaration(
                    "containsAll",
                    CelOverloadDecl.newMemberOverload(
                        "contains_all",
                        "tests whether the list operand contains all the elements of the argument list",
                        SimpleType.BOOL,
                        ListType.create(SimpleType.INT),
                        ListType.create(SimpleType.INT),
                    ),
                    CelOverloadDecl.newMemberOverload(
                        "contains_all",
                        "tests whether the list operand contains all the elements of the argument list",
                        SimpleType.BOOL,
                        ListType.create(SimpleType.STRING),
                        ListType.create(SimpleType.STRING),
                    ),
                ),
                CelFunctionDecl.newFunctionDeclaration(
                    Operator.OLD_IN.function,
                    CelOverloadDecl.newGlobalOverload(
                        "in_list",
                        "list membership",
                        SimpleType.BOOL,
                        typeParamA,
                        listOfA,
                    ),
                ),
                CelFunctionDecl.newFunctionDeclaration(
                    Operator.IN.function,
                    CelOverloadDecl.newGlobalOverload(
                        "in_list",
                        "list membership",
                        SimpleType.BOOL,
                        typeParamA,
                        listOfA,
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
                CelRuntime.CelFunctionBinding.from(
                    "contains_all",
                    List::class.javaObjectType,
                    List::class.javaObjectType,
                ) { x, y -> (x as List<*>).containsAll(y as List<*>) },
                CelRuntime.CelFunctionBinding.from(
                    "in_list",
                    Any::class.javaObjectType,
                    List::class.javaObjectType,
                ) { x, y -> (y as List<*>).contains(x) },
            )

        // Construct the compilation and runtime environments.
        // These instances are immutable and thus trivially thread-safe and amenable to caching.
        private val CEL_RUNTIME: CelRuntime =
            CelRuntimeLegacyImpl.newBuilder()
                .setOptions(celOptions)
                .setStandardEnvironmentEnabled(false)
                .addFunctionBindings(celFunctionBindings)
                .build()

        fun getProgram(
            expression: String,
            varDecls: List<CelVarDecl>? = emptyList(),
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
            varDecls: List<CelVarDecl>?,
        ): CelValidationResult {
            // Compile the expression into an Abstract Syntax Tree.
            val compiler = getCompiler(varDecls ?: emptyList())
            return compiler.compile(expression)
        }

        private fun getCompiler(varDecls: List<CelVarDecl>): CelCompiler {
            return CelCompilerImpl.newBuilder(CelParserImpl.newBuilder(), CelCheckerLegacyImpl.newBuilder())
                .setOptions(celOptions)
                .setStandardEnvironmentEnabled(false)
                .addFunctionDeclarations(celFunctionDecls)
                .addVarDeclarations(varDecls)
                .build()
        }
    }
}
