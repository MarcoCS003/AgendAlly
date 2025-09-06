package com.agendally.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object ScheduleColorsProvider {
    @Composable
    fun getColors(): List<Color> {
        return LocalScheduleColors.current
    }

    @Composable
    fun getRandomColor(): Color {
        val colors = LocalScheduleColors.current
        return if (colors.isNotEmpty()) colors.random() else Color.Gray
    }
}