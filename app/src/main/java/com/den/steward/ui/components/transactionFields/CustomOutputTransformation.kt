package com.den.steward.ui.components.transactionFields

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer

class CustomOutputTransformation : OutputTransformation {

    private val numberRegex = Regex("""\d+(\.\d+)?""")

    @OptIn(ExperimentalFoundationApi::class)
    override fun TextFieldBuffer.transformOutput() {
        val text = originalText
        if (text.isBlank()) return

        val formatted = buildString {
            var lastIndex = 0

            numberRegex.findAll(text).forEach { match ->
                val start = match.range.first
                val end = match.range.last + 1

                // Append operator or symbol before number
                append(text.substring(lastIndex, start))

                val number = match.value.replace(",", "")
                append(formatNumber(number))

                lastIndex = end
            }

            // Append remaining part (operators at end)
            if (lastIndex < text.length) {
                append(text.substring(lastIndex))
            }
        }

        replace(0, length, formatted)
    }

    private fun formatNumber(number: String): String {
        val parts = number.split(".", limit = 2)
        val whole = parts[0]
        val fraction = parts.getOrNull(1)

        val formattedWhole = whole
            .reversed()
            .chunked(3)
            .joinToString(" ")
            .reversed()

        return buildString {
            append(formattedWhole)
            if (fraction != null) {
                append('.')
                append(fraction)
            }
        }
    }
}