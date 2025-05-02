package com.example.academically.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.academically.ui.theme.LocalScheduleColors

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