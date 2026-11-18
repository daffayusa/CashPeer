package com.example.cashpeer.core.ui.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class RupiahVisualTransformation(
    private val includePrefix: Boolean = true
) : VisualTransformation {

    private val prefix = if (includePrefix) "Rp " else ""
    private val prefixLength = prefix.length

    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val n = originalText.length
        val formatted = StringBuilder(prefix)
        val originalToTransformed = IntArray(n + 1)

        var currentTransformed = prefixLength
        for (i in 0 until n) {
            originalToTransformed[i] = currentTransformed
            formatted.append(originalText[i])
            currentTransformed++
            val digitsRemaining = n - 1 - i
            if (digitsRemaining > 0 && digitsRemaining % 3 == 0) {
                formatted.append('.')
                currentTransformed++
            }
        }
        originalToTransformed[n] = currentTransformed

        val transformedString = formatted.toString()
        val transformedLength = transformedString.length

        val transformedToOriginal = IntArray(transformedLength + 1)
        var origIdx = 0
        for (tIdx in 0..transformedLength) {
            while (origIdx < n && originalToTransformed[origIdx + 1] <= tIdx) {
                origIdx++
            }
            transformedToOriginal[tIdx] = origIdx
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return originalToTransformed[offset.coerceIn(0, n)]
            }

            override fun transformedToOriginal(offset: Int): Int {
                return transformedToOriginal[offset.coerceIn(0, transformedLength)]
            }
        }

        return TransformedText(AnnotatedString(transformedString), offsetMapping)
    }
}
