package com.catcher.base.data.dto.validation

import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.validation.ConstraintValidator
import javax.validation.Payload
import javax.validation.Constraint
import javax.validation.ConstraintValidatorContext
import kotlin.reflect.KClass


@Target(AnnotationTarget.TYPE, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [EmailValidator::class])
annotation class ValidEmail(val message: String = "Invalid email",
                            val groups: Array<KClass<*>> = [],
                            val payload: Array<KClass<out Payload>> = [])

private const val PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

class EmailValidator : ConstraintValidator<ValidEmail, String> {
    private var pattern: Pattern? = null
    private var matcher: Matcher? = null

    override fun initialize(constraintAnnotation: ValidEmail?) {}

    override fun isValid(username: String, context: ConstraintValidatorContext): Boolean {
        return validateEmail(username)
    }

    private fun validateEmail(email: String): Boolean {
        pattern = Pattern.compile(PATTERN)
        matcher = pattern!!.matcher(email)
        return matcher!!.matches()
    }
}