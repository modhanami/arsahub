package com.arsahub.backend.services.actionhandlers

import com.arsahub.backend.models.AppUser
import com.arsahub.backend.models.Rule
import com.arsahub.backend.repositories.AppUserRepository
import com.arsahub.backend.services.TemplateEngine
import com.arsahub.backend.services.ruleengine.getCelVarDecls
import dev.cel.common.types.SimpleType
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class ActionAddPointsHandler(private val appUserRepository: AppUserRepository) : ActionHandler {
    private val logger = KotlinLogging.logger {}

    override fun handleAction(
        rule: Rule,
        appUser: AppUser,
        params: Map<String, Any>?,
    ): ActionResult {
        val pointsExpression = rule.actionPointsExpression
        val points = rule.actionPoints
        val previousPoints = appUser.points ?: 0
        if (points != null) {
            val newPoints = previousPoints + points
            appUser.addPoints(points)
            appUserRepository.save(appUser)
            logger.info {
                "User ${appUser.displayName}` (${appUser.userId}) received `$points` points from " +
                    "rule `${rule.title}` (${rule.id}), previous points: $previousPoints, new points: $newPoints"
            }
            return ActionResult.PointsUpdate(previousPoints, newPoints, points)
        }
        if (pointsExpression != null) {
            val evaluatedPoints = evaluateAddPointsExpression(rule, pointsExpression, params ?: emptyMap())
            val newPoints = previousPoints + evaluatedPoints
            appUser.addPoints(evaluatedPoints)
            appUserRepository.save(appUser)
            logger.info {
                "User ${appUser.displayName}` (${appUser.userId}) received `$evaluatedPoints` points from " +
                    "rule `${rule.title}` (${rule.id}), previous points: $previousPoints, new points: $newPoints, " +
                    "points expression: $pointsExpression"
            }
            return ActionResult.PointsUpdate(previousPoints, newPoints, evaluatedPoints)
        }

        throw IllegalArgumentException("Invalid action params: $params, must have either points (integer) or pointsExpression (string)")
    }

    private fun evaluateAddPointsExpression(
        rule: Rule,
        pointsExpression: String,
        params: Map<String, Any>,
    ): Int {
        logger.info { "Checking points expression: $pointsExpression" }
        require(pointsExpression.isNotBlank()) { "Points expression is blank" }
        require(params.isNotEmpty()) { "Params are empty" }

        val varDecls =
            rule.trigger?.fields?.getCelVarDecls() ?: emptyList()

        val programVariables =
            params
                .filter { (key, value) -> value is Int }
                .mapValues { (key, value) ->
                    if (value is Int) {
                        value.toLong()
                    } else {
                        throw IllegalArgumentException("Invalid program variable value: $value")
                    }
                }

        logger.debug { "Program variables: $programVariables" }
        logger.debug { "Var decls: $varDecls" }

        val program = TemplateEngine.getProgram(pointsExpression, varDecls, SimpleType.INT)

        val executionResult =
            program.eval(
                programVariables,
            ) as? Long

        return executionResult?.toInt()
            ?: throw IllegalArgumentException("Invalid points expression")
    }
}
