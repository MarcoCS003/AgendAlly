package com.example.academically.data

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.ui.graphics.Color
import com.example.academically.R
import java.time.LocalDate

data class Career(
    val careerID: Int,
    val name: String,
    val acronym: String,
    val email: String? = null,
    val phone: String? = null
)

data class Institute(
    val instituteID: Int,
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
    var listCareer: List<Career> = emptyList()
)

object SampleInstituteData {
    fun getSampleInstitutes(): List<Institute> {
        return listOf(
            Institute(
                instituteID = 1,
                acronym = "ITP",
                name = "Instituto Tecnológico de Puebla",
                address = "Del Tecnológico 420, Corredor Industrial la Ciénega, 72220 Heroica Puebla de Zaragoza, Pue.",
                email = "info@puebla.tecnm.mx",
                phone = "222 229 8810",
                studentNumber = 6284,
                teacherNumber = 298,
                logo = null,
                webSite = "https://www.puebla.tecnm.mx",
                facebook = "https://www.facebook.com/TecNMPuebla",
                instagram = "https://www.instagram.com/tecnmpuebla",
                youtube = "https://www.youtube.com/user/TECPUEBLA",
                listCareer = listOf(
                    Career(
                        careerID = 1,
                        name = "Ingeniería en Tecnologías de la Información y Comunicaciones",
                        acronym = "TICS",
                    ),
                    Career(
                        careerID = 2,
                        name = "Ingeniería Industrial",
                        acronym = "Ing. Indust",
                    ),
                    Career(
                        careerID = 3,
                        name = "Ingeniería Electrónica",
                        acronym = "Electrónica",
                    ),
                    Career(
                        careerID = 4,
                        name = "Ingeniería Eléctrica",
                        acronym = "Eléctrica",
                    ),
                    Career(
                        careerID = 5,
                        name = "Ingeniería en Gestión Empresarial",
                        acronym = "Gestión Empresarial",
                    ),
                    Career(
                        careerID = 6,
                        name = "Ingeniería Mecánica",
                        acronym = "Mecánica",
                    )
                )
            ),
            Institute(
                instituteID = 2,
                acronym = "ITT",
                name = "Instituto Tecnológico de Tijuana",
                address = "Calzada del Tecnológico S/N, Fraccionamiento Tomas Aquino, 22414 Tijuana, B.C.",
                email = "webmaster@tectijuana.mx",
                phone = "664 607 8400",
                studentNumber = 7500,
                teacherNumber = 350,
                logo = null,
                webSite = "https://www.tijuana.tecnm.mx",
                facebook = "https://www.facebook.com/tectijuana",
                instagram = "https://www.instagram.com/tecnmtijuana",
                youtube = null,
                listCareer = listOf(
                    Career(
                        careerID = 7,
                        name = "Licenciatura en Administración",
                        acronym = "Administración",
                    ),
                    Career(
                        careerID = 8,
                        name = "Ingeniería en Tecnologías de la Información y Comunicaciones",
                        acronym = "TICS",
                    )
                )
            ),
            Institute(
                instituteID = 3,
                acronym = "ITH",
                name = "Instituto Tecnológico de Hermosillo",
                address = "Av. Tecnológico S/N, Col. El Sahuaro, 83170 Hermosillo, Son.",
                email = "contacto@hermosillo.tecnm.mx",
                phone = "662 260 6500",
                studentNumber = 4200,
                teacherNumber = 220,
                logo = null,
                webSite = "https://www.hermosillo.tecnm.mx",
                facebook = "https://www.facebook.com/TecNMHermosillo",
                instagram = null,
                youtube = null,
                listCareer = listOf(
                    Career(
                        careerID = 9,
                        name = "Ingeniería Industrial",
                        acronym = "Ing. Indust",
                    ),
                    Career(
                        careerID = 10,
                        name = "Ingeniería Electrónica",
                        acronym = "Electrónica",
                    )
                )
            ),
            Institute(
                instituteID = 4,
                acronym = "ITT",
                name = "Instituto Tecnológico de Toluca",
                address = "Av. Tecnológico s/n, Agrícola Bella Vista, 52149 Metepec, Méx.",
                email = "webmaster@toluca.tecnm.mx",
                phone = "722 208 7200",
                studentNumber = 5800,
                teacherNumber = 310,
                logo = null,
                webSite = "https://www.toluca.tecnm.mx",
                facebook = null,
                instagram = null,
                youtube = null,
                listCareer = listOf(
                    Career(
                        careerID = 11,
                        name = "Ingeniería Eléctrica",
                        acronym = "Eléctrica",
                    ),
                    Career(
                        careerID = 12,
                        name = "Ingeniería en Gestión Empresarial",
                        acronym = "Gestión Empresarial",
                    )
                )
            ),
            Institute(
                instituteID = 5,
                acronym = "ITSX",
                name = "Instituto Tecnológico Superior de Xalapa",
                address = "Sección 5A, Reserva Territorial, 91060 Xalapa, Ver.",
                email = "contacto@itsx.edu.mx",
                phone = "228 165 0525",
                studentNumber = 3800,
                teacherNumber = 190,
                logo = null,
                webSite = "https://www.itsx.edu.mx",
                facebook = "https://www.facebook.com/ITSXalapa",
                instagram = "https://www.instagram.com/itsxalapa",
                youtube = "https://www.youtube.com/user/ITSXalapa",
                listCareer = listOf(
                    Career(
                        careerID = 13,
                        name = "Ingeniería Mecánica",
                        acronym = "Mecánica",
                    ),
                    Career(
                        careerID = 14,
                        name = "Licenciatura en Administración",
                        acronym = "Administración",
                    )
                )
            )
        )
    }
}

object BlogDataExample{
    @RequiresApi(Build.VERSION_CODES.O)
    fun getSampleBlog(): List<Event>{
        return listOf(
            Event(
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
                category = EventCategory.INSTITUTIONAL,
                color = Color(0xFF00BCD4), // Cian
                items = listOf(
                    EventItem(
                        1,
                        Icons.Default.AttachFile,
                        "Inovatecm.2025.pdf"
                    ),
                    EventItem(2, Icons.Default.Call, "123456789")
                ),
                notification = EventNotification(
                    id = 1,
                    time = 86400000, // 1 día
                    title = "Recordatorio",
                    message = "Convocatoria Servicio Social mañana",
                    isEnabled = true
                )),
            Event(
                id = 2,
                title = "Congreso Internacional en agua limpia y saneamiento del TECNM",
                shortDescription = "Registro para estudiantes",
                longDescription = "Participa en el 1er. Congreso Internacional de Agua Limpia y Saneamiento del TECNM",
                location = "Modalidad Híbrida",
                startDate = LocalDate.of(2025, 9, 25),
                endDate = LocalDate.of(2025, 9, 26),
                category = EventCategory.INSTITUTIONAL,
                imagePath = R.drawable.congreso.toString(),
                color = Color(0xFF2196F3)
            ),
            Event(
                id = 3,
                title = "Concurso de Programación 2025",
                shortDescription = "Para estudiantes de TICS",
                longDescription = "Invitación a los estudiantes de TICS a participar en el concurso de programación de 2025 sin costo",
                location = "Edificio 36",
                startDate = LocalDate.of(2025, 4, 28),
                endDate = LocalDate.of(2025, 4, 28),
                category = EventCategory.CAREER,
                imagePath = R.drawable.concurso.toString(),
                color = Color(0xFF4CAF50),
                items = listOf(
                    EventItem(
                        1,
                        Icons.Default.AttachFile,
                        "ConcursoProgramacion.2025.pdf"
                    ),
                    EventItem(2, Icons.Default.AccessTime, "8:30-15:30")
                ),
            ),
            Event(
                id = 4,
                title = "Jornadas de TICS 2025",
                shortDescription = "Conferencias internacionales",
                longDescription = "Participa en las jornadas de TICS del año 2025 con conferencistas internacionales, estaremos enfocados en el auge de la inteligencia artificial, ciencia de datos y las tecnologías emergentes para el desarrollo web.",
                location = "Edificio 53",
                startDate = LocalDate.of(2025, 9, 15),
                endDate = LocalDate.of(2025, 9, 15),
                category = EventCategory.CAREER,
                color = Color(0xFF4CAF50)
            ),
            Event(
                id = 5,
                title = "Plática de Servicio Social",
                shortDescription = "Información importante",
                longDescription = "Información sobre los requisitos y proceso para realizar el servicio social",
                startDate = LocalDate.of(2025, 5, 10),
                endDate = LocalDate.of(2025, 5, 10),
                category = EventCategory.CAREER,
                color = Color(0xFFFFAB00),
                notification = EventNotification(
                    id = 1,
                    time = 86400000,
                    title = "Recordatorio",
                    message = "Plática de servicio social mañana",
                    isEnabled = true
                )
            )
        )
    }
}