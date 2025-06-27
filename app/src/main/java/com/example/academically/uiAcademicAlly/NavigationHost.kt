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
import com.example.academically.ViewModel.BlogEventsViewModel
import com.example.academically.ViewModel.EventViewModel
import com.example.academically.ViewModel.InstituteViewModel
import com.example.academically.ViewModel.ScheduleViewModel
import com.example.academically.ViewModel.ScheduleViewModelFactory
import com.example.academically.data.BlogDataExample
import com.example.academically.data.Career
import com.example.academically.data.Institute
import com.example.academically.data.SampleInstituteData
import com.example.academically.data.api.ApiService
import com.example.academically.data.database.AcademicAllyDatabase
import com.example.academically.data.repository.EventRepository
import com.example.academically.data.repositorty.ScheduleRepository
import com.example.academically.uiAcademicAlly.calendar.AddEventScreenWithViewModel
import com.example.academically.uiAcademicAlly.calendar.CalendarScreenWithViewModel
import com.example.academically.uiAcademicAlly.calendar.EditEventScreenWithViewModel
import com.example.academically.uiAcademicAlly.calendar.TabletCalendarScreen
import com.example.academically.uiAcademicAlly.institute.EventBlogScreen
import com.example.academically.uiAcademicAlly.institute.EventBlogScreenWithAPI
import com.example.academically.uiAcademicAlly.institute.OrganizationsScreen
import com.example.academically.uiAcademicAlly.institute.TabletEventBlogScreen
import com.example.academically.uiAcademicAlly.schedule.AddScheduleActivityScreenWithViewModel
import com.example.academically.uiAcademicAlly.schedule.EditScheduleActivityScreen
import com.example.academically.uiAcademicAlly.schedule.ScheduleScreenWithViewModel
import com.example.academically.uiAcademicAlly.schedule.TabletScheduleScreen
import com.example.academically.uiAcademicAlly.settings.ConfigurationScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationHost(
    navController: NavHostController,
    isTablet: Boolean = false
) {

    val context = LocalContext.current
    val database = AcademicAllyDatabase.getDatabase(context)

    // Servicios API
    val apiService = remember { ApiService() }

    // Repositorios
    val scheduleRepository = ScheduleRepository(database.scheduleDao())
    val eventRepository = EventRepository(database.eventDao())
    // View Models
    val scheduleViewModel: ScheduleViewModel = viewModel(
        factory = ScheduleViewModelFactory(scheduleRepository)
    )
    val eventViewModel: EventViewModel = viewModel(
        factory = EventViewModel.Factory(eventRepository)
    )
    // Institutes ViewModel
    val instituteViewModel: InstituteViewModel = viewModel()
    // Blog ViewModel
    val blogEventsViewModel: BlogEventsViewModel = viewModel(
        factory = BlogEventsViewModel.Factory(apiService)
    )


    NavHost(navController = navController, startDestination = NavigationItemContent.Calendar.ruta) {
        composable(NavigationItemContent.Calendar.ruta) {
            // Modificado para pasar la función de navegación al FAB
            if (isTablet) {
                TabletCalendarScreen()
            } else {
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
        composable(NavigationItemContent.Settings.ruta) {
            ConfigurationScreen()
        }

        composable(NavigationItemContent.Institute.ruta) {

            OrganizationsScreen(
                onOrganizationClick = {
                    navController.navigate(NavigationItemContent.BlogOrganization.ruta)
                },
                onAddOrganizationClick = {
                    navController.navigate(NavigationItemContent.AddOrganization.ruta)
                }
            )
        }

        /*composable(NavigationItemContent.AddOrganization.ruta) {
            // Lista mutable para institutos y carreras seleccionados
            val selectedInstitutesWithCareers = remember {
                mutableStateListOf<Pair<Institute, Career>>()
            }

            InstituteScreenWithAPI(
                viewModel = instituteViewModel,
                onInstituteAndCareerSelected = { institute, career ->
                    // Agregar la pareja de instituto y carrera a la lista de seleccionados
                    val pair = institute to career
                    if (!selectedInstitutesWithCareers.contains(pair)) {
                        selectedInstitutesWithCareers.add(pair)
                    }

                    // Navegar de vuelta a organizaciones
                    navController.navigateUp()
                }
            )
        }*/


        composable(NavigationItemContent.BlogOrganization.ruta) {
            if (isTablet) {
                TabletEventBlogScreen(
                    events = BlogDataExample.getSampleBlog(),
                    eventViewModel = eventViewModel
                )
            } else {
                // ACTUALIZADO: Usar la versión mejorada con tu diseño original + API
                EventBlogScreen(
                    blogEventsViewModel = blogEventsViewModel,
                    eventViewModel = eventViewModel
                )
            }
        }

        composable(NavigationItemContent.Schedule.ruta) {
            if (isTablet) {
                TabletScheduleScreen()
            } else {
                ScheduleScreenWithViewModel(
                    viewModel = scheduleViewModel,
                    onAddActivity = {
                        navController.navigate(NavigationItemContent.AddEventSchedule.ruta)
                    },
                    onEditActivity = { schedule ->
                        navController.navigate("${NavigationItemContent.EditEventSchedule.ruta}/${schedule.id}")
                    }
                )
            }
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
