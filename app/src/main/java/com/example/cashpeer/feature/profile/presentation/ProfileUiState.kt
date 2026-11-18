package com.example.cashpeer.feature.profile.presentation

import com.example.cashpeer.core.ui.theme.AppThemeMode

data class ProfileUiState(
    val userName: String = "",
    val isEditingName: Boolean = false,
    val editNameInput: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val isExporting: Boolean = false,
    val exportMessage: String? = null
)
