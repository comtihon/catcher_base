package com.catcher.base.data.dto.validation

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass


@Target(AnnotationTarget.TYPE, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [TeamValidator::class])
annotation class ValidTeamName(val message: String = "Invalid team name",
                            val groups: Array<KClass<*>> = [],
                            val payload: Array<KClass<out Payload>> = [])

class TeamValidator : ConstraintValidator<ValidTeamName, String> {

    override fun initialize(constraintAnnotation: ValidTeamName?) {}

    override fun isValid(teamName: String, context: ConstraintValidatorContext): Boolean {
        return teamName != "my" // my is reserved
    }
}