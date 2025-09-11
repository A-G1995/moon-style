package org.example.moonstyle.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [DigitsOnlyValidator::class])
annotation class DigitsOnly(
    val message: String = "باید فقط رقم باشد",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)