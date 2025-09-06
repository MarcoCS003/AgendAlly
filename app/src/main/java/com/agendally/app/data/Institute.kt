package com.agendally.app.data

import androidx.compose.ui.graphics.Color
import java.time.LocalDate


data class EventInstitute(
    val id: Int,
    // Datos básicos
    val title: String,
    val shortDescription: String = "",
    val longDescription: String = "",
    val location: String = "",
    val color: Color,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val category: EventCategory = EventCategory.PERSONAL,
    val imagePath: String = "",
    val items: List<EventItem> = emptyList(),
    val notification: EventNotification? = null,
    val mesID: Int? = null,
    val shape: EventShape = EventShape.RoundedFull,
    // NUEVO: ID del instituto al que pertenece el evento
    val instituteId: Int? = null
)

