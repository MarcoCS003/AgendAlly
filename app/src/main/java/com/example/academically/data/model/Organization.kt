package com.example.academically.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import com.example.academically.R
import java.time.LocalDate

enum class ChannelType {
    CAREER,         // Ingeniería en TICS, Industrial, etc.
    DEPARTMENT,     // Biblioteca, Centro de Idiomas, etc.
    ADMINISTRATIVE  // Servicios Escolares, Recursos Humanos, etc.
}

data class Channel(
    val channelID: Int,
    val organizationId: Int, // FK a Organization
    val name: String,
    val acronym: String,
    val description: String = "",
    val type: ChannelType, // CAREER, DEPARTMENT, ADMINISTRATIVE
    val email: String? = null,
    val phone: String? = null,
    val isActive: Boolean = true
)
data class Organization(
    val organizationID: Int,
    val acronym: String,
    val name: String,
    val address: String,
    val email: String,
    val phone: String,
    val studentNumber: Int,
    val teacherNumber: Int,
    var logo: Int? = null,
    var webSite: String? = null,
    var facebook: String? = null,
    var instagram: String? = null,
    var twitter: String? = null,
    var youtube: String? = null,
    var channels: List<Channel> = emptyList() // ✅ CAMBIO: Career -> Channel
)



data class EventOrganization(
    val id: Int,
    val title: String,
    val shortDescription: String = "",
    val longDescription: String = "",
    val location: String = "",
    val color: Color,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val category: PersonalEventType = PersonalEventType.SUBSCRIBED,
    val imagePath: String = "",
    val items: List<PersonalEventItem> = emptyList(),
    val notification: PersonalEventNotification? = null,
    val mesID: Int? = null,
    val shape: EventShape = EventShape.RoundedFull,
    val channelId: Int? = null,
    val organizationId: Int? = null
)


object BlogDataExample {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getSampleBlog(): List<EventOrganization> {
        return listOf(
            EventOrganization(
                id = 1,
                title = "INNOVATECNMN 2025",
                shortDescription = "Registro para estudiantes lider",
                longDescription = "Cumbre nacional de desarrollo tecnológico,\n" +
                        "investigación e innovación INOVATECNM\u2028 \u2028Dirigida al estudiantado inscrito al periodo Enero-Junio 2025 personal docente y de investigación del Instituto Tecnológico de Puebla\n" +
                        "\n" +
                        "5 eventos simultáneos:\n" +
                        "Certamen de Proyectos\n" +
                        "HackaTec\n" +
                        "Cortometraje de InnvAcción\n" +
                        "Retos de Transformacionales\n" +
                        "\n" +
                        "Local : 23 de Mayo\n" +
                        "Regional : Septiembre 2025\n" +
                        "Nacional : Noviembre 2025\n" +
                        "\n" +
                        "Criterios de Evaluación\n" +
                        "Memoria Tecnica \n" +
                        "Prototipo \n" +
                        "lo que señale el modelo de operación\n" +
                        "del innovatecnm de 2025 de acuerdo a cada evento/categoría correspondiente  ",
                location = "Edificio 53",
                imagePath = R.drawable.inovatecnm.toString(),
                startDate = LocalDate.of(2025, 11, 28),
                endDate = LocalDate.of(2025, 11, 29),
                category = PersonalEventType.SUBSCRIBED,
                color = Color(0xFF00BCD4), // Cian
                // ITP
            ),
            EventOrganization(
                id = 2,
                title = "Congreso Internacional en agua limpia y saneamiento del TECNM",
                shortDescription = "Registro para estudiantes",
                longDescription = "Participa en el 1er. Congreso Internacional de Agua Limpia y Saneamiento del TECNM",
                location = "Modalidad Híbrida",
                startDate = LocalDate.of(2025, 9, 25),
                endDate = LocalDate.of(2025, 9, 26),
                category = PersonalEventType.SUBSCRIBED,
                imagePath = R.drawable.congreso.toString(),
                color = Color(0xFF2196F3),

            ),
            EventOrganization(
                id = 3,
                title = "Concurso de Programación 2025",
                shortDescription = "Para estudiantes de TICS",
                longDescription = "Invitación a los estudiantes de TICS a participar en el concurso de programación de 2025 sin costo",
                location = "Edificio 36",
                startDate = LocalDate.of(2025, 4, 28),
                endDate = LocalDate.of(2025, 4, 28),
                category = PersonalEventType.SUBSCRIBED,
                imagePath = R.drawable.concurso.toString(),
                color = Color(0xFF4CAF50),


            ),
            EventOrganization(
                id = 4,
                title = "Jornadas de TICS 2025",
                shortDescription = "Conferencias internacionales",
                longDescription = "Participa en las jornadas de TICS del año 2025 con conferencistas internacionales, estaremos enfocados en el auge de la inteligencia artificial, ciencia de datos y las tecnologías emergentes para el desarrollo web.",
                location = "Edificio 53",
                startDate = LocalDate.of(2025, 9, 15),
                endDate = LocalDate.of(2025, 9, 15),
                category = PersonalEventType.SUBSCRIBED,
                color = Color(0xFF4CAF50),

            ),
            EventOrganization(
                id = 5,
                title = "Plática de Servicio Social",
                shortDescription = "Información importante",
                longDescription = "Información sobre los requisitos y proceso para realizar el servicio social",
                startDate = LocalDate.of(2025, 5, 10),
                endDate = LocalDate.of(2025, 5, 10),
                category = PersonalEventType.SUBSCRIBED,
                color = Color(0xFFFFAB00),

            )
        )
    }
}