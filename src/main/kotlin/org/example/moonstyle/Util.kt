package org.example.moonstyle

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = false) // keep explicit; we'll add @Convert on chosen fields
class DigitsToEnglishConverter : AttributeConverter<String, String> {
    
    private fun normalize(input: String?): String? {
        if (input == null) return null
        val out = StringBuilder(input.length)
        for (ch in input) {
            when {
                ch in '۰'..'۹' -> out.append(('0'.code + (ch.code - '۰'.code)).toChar()) // Persian digits
                ch in '٠'..'٩' -> out.append(('0'.code + (ch.code - '٠'.code)).toChar()) // Arabic-Indic digits
                else -> out.append(ch)
            }
        }
        return out.toString()
    }
    
    override fun convertToDatabaseColumn(attribute: String?): String? =
        normalize(attribute)
    
    override fun convertToEntityAttribute(dbData: String?): String? =
        normalize(dbData) // ensure entity always sees normalized digits too
}