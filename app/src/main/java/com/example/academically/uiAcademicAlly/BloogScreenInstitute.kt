package com.example.academically.uiAcademicAlly

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.academically.R
import com.example.academically.data.Event
import com.example.academically.data.EventCategory
import com.example.academically.data.EventItem
import com.example.academically.data.EventNotification
import java.time.LocalDate

@Composable
fun EventCardBlog(
    event: Event,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(

            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = event.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))


            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) { // Imagen del Evento

                //Imagen si existe
                if (event.imagePath.isNotEmpty()) {
                    Image(
                        painter = painterResource(id = event.imagePath.toInt()),
                        contentDescription = "Imagen del evento",
                        modifier = Modifier
                            .size(150.dp)
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                    )

                }

                Column(
                    modifier = Modifier.height(150.dp),
                    verticalArrangement = Arrangement.Center
                ) { // Información de fecha
                    EventInfoItem(
                        icon = Icons.Default.DateRange,
                        text = formatEventDate(event.startDate, event.endDate)
                    )

                    // Ubicación si existe
                    if (event.location.isNotEmpty()) {
                        EventInfoItem(
                            icon = Icons.Default.LocationOn,
                            text = event.location
                        )
                    }
                }
            }

            // Descripción larga
            event.longDescription.let { longDescription ->
                val truncatedDescription = if (event.longDescription.length > 150) {
                    longDescription.substring(0, 150) + "..."
                } else {
                    longDescription
                }

                Text(
                    text = truncatedDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

        }
    }
}

@Composable
fun EventDetailCardBlog(
    event: Event,
    onDismiss: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(

                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                // Encabezado con categoría y título
                Text(text = event.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))


                // Imagen del Evento
                if (event.imagePath.isNotEmpty()) {
                    Image(
                        painter = painterResource(id = event.imagePath.toInt()),
                        contentDescription = "Imagen del evento",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                    )
                    Spacer(Modifier.padding(5.dp))
                }

                // Descripción larga si existe
                if (event.longDescription.isNotEmpty()) {
                    Text(
                        text = event.longDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                // Información de fecha
                EventInfoItem(
                    icon = Icons.Default.DateRange,
                    text = formatEventDate(event.startDate, event.endDate)
                )

                // Ubicación si existe
                if (event.location.isNotEmpty()) {
                    EventInfoItem(
                        icon = Icons.Default.LocationOn,
                        text = event.location
                    )
                }

                // Espacio para elementos adicionales
                if (event.items.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Mostrar cada item del evento
                    event.items.forEach { item ->
                        EventInfoItem(
                            icon = item.icon,
                            text = item.text
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Botón de ocultar
                    Button(
                        onClick = onDismiss,

                    ) {
                        Text("Añadir a Calendario")
                    }
                }
            }
        }
    }
}


@SuppressLint("NewApi")
@Preview(showBackground = true)
@Composable
fun CardEventBlogPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Ejemplo 1: Evento con imagen y ubicación
            EventDetailCardBlog(
                event = Event(
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
                    imagePath = R.drawable.seminario.toString(),
                    startDate = LocalDate.of(2025, 11, 28),
                    endDate = LocalDate.of(2025, 11, 29),
                    category = EventCategory.CAREER,
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
                    )
                )
            )
            EventCardBlog(
                Event(
                    id = 2,
                    title = "Sesión de Estudio para Examen Final",
                    shortDescription = "Preparación examen",
                    longDescription = "Tengo examen final de Programación y quiero repasar bien los temas. Voy a hacer ejercicios, revisar apuntes y usar tarjetas de memoria para recordar mejor. También quiero resolver dudas y practicar con preguntas tipo examen.",
                    location = "Biblioteca Central",
                    startDate = LocalDate.of(2025, 6, 10),
                    endDate = LocalDate.of(2025, 6, 10),
                    category = EventCategory.PERSONAL,
                    color = Color(0xFFE91E63), // Rosa
                    notification = EventNotification(
                        id = 2,
                        time = 3600000, // 1 hora
                        title = "Recordatorio",
                        message = "Sesión de estudio en 1 hora",
                        isEnabled = false
                    )
                ),
                modifier = Modifier
            )

        }
    }
}