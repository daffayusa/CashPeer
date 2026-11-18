package com.example.cashpeer.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.example.cashpeer.core.ui.transformation.RupiahVisualTransformation

@Composable
fun CpRupiahTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Nominal",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String? = "0",
    singleLine: Boolean = true,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false
) {
    val cleanDigits = value.filter { it.isDigit() }

    CpTextField(
        value = cleanDigits,
        onValueChange = { raw ->
            val digits = raw.filter { it.isDigit() }
            onValueChange(digits)
        },
        label = label,
        placeholder = placeholder,
        modifier = modifier,
        enabled = enabled,
        singleLine = singleLine,
        keyboardType = KeyboardType.Number,
        visualTransformation = RupiahVisualTransformation(includePrefix = true),
        supportingText = supportingText,
        isError = isError
    )
}
