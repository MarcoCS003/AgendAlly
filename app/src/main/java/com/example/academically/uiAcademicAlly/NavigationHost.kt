package com.example.academically.uiAcademicAlly

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.academically.R
import com.example.academically.data.BlogDataExample
import com.example.academically.data.Career
import com.example.academically.data.Event
import com.example.academically.data.EventCategory
import com.example.academically.data.EventItem
import com.example.academically.data.EventNotification
import com.example.academically.data.EventProcessor
import com.example.academically.data.EventShape
import com.example.academically.data.Institute
import com.example.academically.data.ProcessedEvent
import com.example.academically.data.SampleInstituteData
import com.example.academically.data.SampleScheduleData
import com.example.academically.data.SystemCalendarProvider
import java.lang.reflect.Modifier
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationHost(navController: NavHostController){
    NavHost(navController = navController, startDestination = NavigationItemContent.Calendar.ruta ,){
        composable(NavigationItemContent.Calendar.ruta){
            val events = listOf(
                // Eventos originales convertidos al nuevo formato
                Event(
                    id = 1,
                    color = Color.Blue,
                    title = "Suspension de clases",
                    startDate = LocalDate.of(2025, 1, 1),
                    endDate = LocalDate.of(2025, 1, 1),
                    mesID = 1
                ),
                Event(
                    id = 2,
                    color = Color.Blue,
                    title = "Suspension de clases",
                    startDate = LocalDate.of(2025, 2, 3),
                    endDate = LocalDate.of(2025, 2, 3),
                    mesID = 1
                ),
                Event(
                    id = 3,
                    color = Color.Blue,
                    title = "Suspension de clases",
                    startDate = LocalDate.of(2025, 3, 17),
                    endDate = LocalDate.of(2025, 3, 17),
                    mesID = 1
                ),
                Event(
                    id = 4,
                    color = Color.Green,
                    title = "Periodo vacacional",
                    startDate = LocalDate.of(2025, 1, 1),
                    endDate = LocalDate.of(2025, 1, 8),
                    mesID = 1
                ),
                Event(
                    id = 5,
                    color = Color.Green,
                    title = "Periodo vacacional",
                    startDate = LocalDate.of(2025, 4, 14),
                    endDate = LocalDate.of(2025, 4, 25),
                    mesID = 1
                ),
                Event(
                    id = 6,
                    color = Color.Black,
                    title = "Inicio de clases",
                    startDate = LocalDate.of(2025, 1, 28),
                    endDate = LocalDate.of(2025, 1, 28),
                    mesID = 5,
                    shape = EventShape.Circle
                ),
                Event(
                    id = 7,
                    color = Color.Magenta,
                    title = "Mi cumpleaños",
                    startDate = LocalDate.of(2025, 2, 28),
                    endDate = LocalDate.of(2025, 2, 28),
                    mesID = 2,
                    shape = EventShape.Circle
                ),
                // Evento que abarca varios meses
                Event(
                    id =8 ,
                    color = Color.Blue,
                    title = "Suspension de clases",
                    startDate = LocalDate.of(2025, 5, 1),
                    endDate = LocalDate.of(2025, 5, 1),
                    mesID = 1
                ), Event(
                    id = 9,
                    color = Color.Blue,
                    title = "Suspension de clases",
                    startDate = LocalDate.of(2025, 5, 5),
                    endDate = LocalDate.of(2025, 5, 5),
                    mesID = 1
                ), Event(
                    id = 10,
                    color = Color.Blue,
                    title = "Suspension de clases",
                    startDate = LocalDate.of(2025, 5, 15),
                    endDate = LocalDate.of(2025, 5, 15),
                    mesID = 1
                ),
                Event(
                    id = 11,
                    color = Color.Gray,
                    title = "Suspension de clases",
                    startDate = LocalDate.of(2025, 5, 15),
                    endDate = LocalDate.of(2025, 5, 15),
                    mesID = 1
                ),
                Event(
                    id = 12,
                    color = Color.Yellow,
                    title = "Suspension de clases",
                    startDate = LocalDate.of(2025, 5, 15),
                    endDate = LocalDate.of(2025, 5, 15),
                    mesID = 1
                ),
                Event(
                    id = 13,
                    color = Color.Gray,
                    title = "Fin de clases",
                    startDate = LocalDate.of(2025, 5, 30),
                    endDate = LocalDate.of(2025, 5, 30),
                    mesID = 1
                ),
                Event(
                    id = 14,
                    title = "Convocatoria Servicio Social",
                    shortDescription = "Registro para servicio",
                    longDescription = "Estimado estudiante de TICs si le interesa realizar su servicio social durante el periodo Diciembre 2024 - Junio 2025 guardar esta información Coordinación Instruccional de tutorías Desarrollo Académico.",
                    location = "Edificio 6",
                    imagePath = R.drawable.seminario.toString(),
                    startDate = LocalDate.of(2025, 11, 28),
                    endDate = LocalDate.of(2025, 11, 29),
                    category = EventCategory.CAREER,
                    color = Color(0xFF00BCD4), // Cian
                    items = listOf(
                        EventItem(1, Icons.Default.Person, "Coordinación Instruccional de tutorías"),
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

            // Crear proveedor de calendario
            val calendarProvider = SystemCalendarProvider(LocalContext.current)

            // Obtener datos de los meses
            val months = calendarProvider.getMonthsData()

            // Mes actual
            val currentMonthIndex = calendarProvider.getCurrentMonthIndex()

            // Procesar eventos para todos los meses
            val allProcessedEvents = mutableMapOf<Int, Map<Int, ProcessedEvent>>()

            // Preparar eventos procesados para cada mes
            for (month in months) {
                val monthEvents = EventProcessor.processEvents(
                    month.id,
                    2025,
                    events
                )
                allProcessedEvents[month.id] = monthEvents
            }
            CalendarAppScreen(
                mounts = months,
                currentMonthIndex = currentMonthIndex,
                processedEvents = allProcessedEvents,
                onAddEventClick = {navController.navigate(NavigationItemContent.AddEvent.ruta)}
            )
        }

        composable(NavigationItemContent.AddEvent.ruta) {

            AddEventScreen(
                onBack = {
                    navController.navigateUp()
                },
                onAddEvent = { title, date, location, color, description ->
                    // Aquí implementarás la lógica para añadir el evento
                    // Por ahora solo navegamos de vuelta al calendario
                    navController.navigateUp()
                }
            )
        }

        composable(NavigationItemContent.Next.ruta){
            val availableInstitutes = SampleInstituteData.getSampleInstitutes()

            // Lista mutable para institutos y carreras seleccionados
            val selectedInstitutesWithCareers = remember {
                mutableStateListOf<Pair<Institute, Career>>()
            }


            InstituteAndCareerSelectionFlow(
                institutes = availableInstitutes,
                onInstituteAndCareerSelected = { institute, career ->
                    // Agregar la pareja de instituto y carrera a la lista de seleccionados
                    val pair = institute to career
                    if (!selectedInstitutesWithCareers.contains(pair)) {
                        selectedInstitutesWithCareers.add(pair)
                    }

                    // Aquí se podría implementar la navegación a la siguiente pantalla
                    // o cualquier otra acción después de la selección
                }
            )

        }

        composable (NavigationItemContent.Institute.ruta){
            EventBlogScreen(
                events = BlogDataExample.getSampleBlog())
        }

        composable(NavigationItemContent.Schedule.ruta) {
            ScheduleScreen(
                schedules = SampleScheduleData.getSampleSchedules()
            )
        }
    }
}