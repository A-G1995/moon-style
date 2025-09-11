package org.example.moonstyle.validation


import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class DigitsOnlyValidator : ConstraintValidator<DigitsOnly, CharSequence?> {
    
    override fun isValid(value: CharSequence?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true // let @NotBlank handle null/empty
        
        // Normalize Persian (۰-۹) and Arabic-Indic (٠-٩) digits to ASCII 0-9
        val normalized = buildString(value.length) {
            for (ch in value) {
                when {
                    ch in '۰'..'۹' -> append(('0'.code + (ch.code - '۰'.code)).toChar())
                    ch in '٠'..'٩' -> append(('0'.code + (ch.code - '٠'.code)).toChar())
                    else -> append(ch)
                }
            }
        }
        
        // Only digits allowed after normalization
        return normalized.matches(Regex("""^\d+$"""))
    }
}