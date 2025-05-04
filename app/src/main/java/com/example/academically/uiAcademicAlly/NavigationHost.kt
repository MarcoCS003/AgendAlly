
package com.example.academically.uiAcademicAlly

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.academically.ViewModel.EventViewModel
import com.example.academically.ViewModel.ScheduleViewModel
import com.example.academically.ViewModel.ScheduleViewModelFactory
import com.example.academically.data.BlogDataExample
import com.example.academically.data.Career
import com.example.academically.data.Institute
import com.example.academically.data.SampleInstituteData
import com.example.academically.data.database.AcademicAllyDatabase
import com.example.academically.data.repository.EventRepository
import com.example.academically.data.repositorty.ScheduleRepository
import com.example.academically.uiAcademicAlly.calendar.AddEventScreenWithViewModel
import com.example.academically.uiAcademicAlly.calendar.CalendarScreenWithViewModel
import com.example.academically.uiAcademicAlly.calendar.EditEventScreenWithViewModel
import com.example.academically.uiAcademicAlly.institute.EventBlogScreen
import com.example.academically.uiAcademicAlly.institute.InstituteAndCareerSelectionFlow
import com.example.academically.uiAcademicAlly.schedule.AddScheduleActivityScreenWithViewModel
import com.example.academically.uiAcademicAlly.schedule.EditScheduleActivityScreen
import com.example.academically.uiAcademicAlly.schedule.ScheduleScreenWithViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationHost(navController: NavHostController) {

    val context = LocalContext.current
    val database = AcademicAllyDatabase.getDatabase(context)

    // Repositorios
    val scheduleRepository = ScheduleRepository(database.scheduleDao())
    val eventRepository = EventRepository(database.eventDao())

    // ViewModels
    val scheduleViewModel: ScheduleViewModel = viewModel(
        factory = ScheduleViewModelFactory(scheduleRepository)
    )
    // Crear y usar un único EventViewModel para toda la navegación
    val eventViewModel: EventViewModel = viewModel(
        factory = EventViewModel.Factory(eventRepository)
    )


    NavHost(navController = navController, startDestination = NavigationItemContent.Calendar.ruta) {
        composable(NavigationItemContent.Calendar.ruta) {
            // Modificado para pasar la función de navegación al FAB
            CalendarScreenWithViewModel(
                viewModel = eventViewModel,
                onAddEventClick = {
                    navController.navigate(NavigationItemContent.AddEvent.ruta)
                },
                onEditEventClick = { event ->
                    // Navegar a la pantalla de edición con el ID del evento
                    navController.navigate("${NavigationItemContent.EditEvent.ruta}/${event.id}")
                }
            )
        }

        composable(NavigationItemContent.AddEvent.ruta) {
            // Mantenemos la navegación de vuelta
            AddEventScreenWithViewModel(
                viewModel = eventViewModel,
                onBack = {
                    navController.navigateUp()
                }
            )
        }

        // Nueva ruta para la edición de eventos
        composable("${NavigationItemContent.EditEvent.ruta}/{eventId}") { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()
            println("DEBUG: Intentando navegar a edición con ID: $eventId")
            if (eventId != null) {
                EditEventScreenWithViewModel(
                    eventId = eventId,
                    viewModel = eventViewModel, // Pasar el viewModel
                    onBack = {
                        println("DEBUG: Volviendo de pantalla de edición")
                        navController.navigateUp()
                    }
                )
            } else {
                println("DEBUG: ID de evento nulo, volviendo")
                navController.navigateUp()
            }
        }

        // El resto del código se mantiene igual
        composable(NavigationItemContent.Next.ruta) {
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
                }
            )
        }

        composable(NavigationItemContent.Institute.ruta) {
            EventBlogScreen(
                events = BlogDataExample.getSampleBlog()
            )
        }

        composable(NavigationItemContent.Schedule.ruta) {
            ScheduleScreenWithViewModel(
                viewModel = scheduleViewModel,
                onAddActivity = {
                    navController.navigate(NavigationItemContent.AddEventSchedule.ruta)
                },
                onEditActivity = { schedule ->
                    // Solo pasar el ID, no el objeto completo
                    navController.navigate("${NavigationItemContent.EditEventSchedule.ruta}/${schedule.id}")
                }
            )
        }

        composable("${NavigationItemContent.EditEventSchedule.ruta}/{scheduleId}") { backStackEntry ->
            val scheduleId = backStackEntry.arguments?.getString("scheduleId")?.toIntOrNull()
            if (scheduleId != null) {
                EditScheduleActivityScreen(
                    scheduleId = scheduleId,
                    viewModel = scheduleViewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            } else {
                navController.navigateUp()
            }
        }

        composable(NavigationItemContent.AddEventSchedule.ruta) {
            AddScheduleActivityScreenWithViewModel(
                viewModel = scheduleViewModel,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}
