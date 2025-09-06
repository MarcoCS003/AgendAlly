package com.agendally.app.data


import android.os.Build
import androidx.annotation.RequiresApi
import com.agendally.app.data.api.EventInstituteBlog
import java.time.LocalDate


object EventAdapter {

    /**
     * Convierte EventInstituteBlog del servidor → Event local (para calendario)
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun fromCalendarEvent(
        serverEvent: EventInstituteBlog,
        organizationId: Int
    ): Event{
        return Event(
            id = 0, // Room auto-generará el ID
            title = serverEvent.title,
            longDescription = serverEvent.longDescription.ifEmpty { serverEvent.shortDescription },
            location = serverEvent.location,
            startDate = LocalDate.parse(serverEvent.startDate),
            endDate = LocalDate.parse(serverEvent.endDate),
            colorIndex = serverEvent.colorIndex?: 1,
            category = EventCategory.CALENDAR_EVENT,
            organizationId = serverEvent.organizationId,
            shortDescription = serverEvent.shortDescription,
            imagePath = "",
            isActive = true,
        )
    }

}