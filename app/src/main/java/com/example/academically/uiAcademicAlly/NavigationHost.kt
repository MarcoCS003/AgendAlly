package com.example.academically.uiAcademicAlly


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.academically.ui.screens.auth.RegisterScreen
import com.academically.ui.screens.auth.RegisterScreenWithViewModel
import com.example.academically.ViewModel.*
import com.example.academically.data.model.BlogDataExample
import com.example.academically.data.model.Career
import com.example.academically.data.model.Institute
import com.example.academically.data.model.UserRole
import com.example.academically.data.remote.api.ApiService
import com.example.academically.data.local.database.AcademicAllyDatabase
import com.example.academically.data.repository.PersonalEventRepository
import com.example.academically.data.repositorty.AuthRepository
import com.example.academically.data.repositorty.ScheduleRepository
import com.example.academically.uiAcademicAlly.calendar.AddEventScreenWithViewModel
import com.example.academically.uiAcademicAlly.calendar.CalendarScreenWithViewModel
import com.example.academically.uiAcademicAlly.calendar.EditEventScreenWithViewModel
import com.example.academically.uiAcademicAlly.calendar.TabletCalendarScreen
import com.example.academically.uiAcademicAlly.institute.EventBlogScreen
import com.example.academically.uiAcademicAlly.institute.InstituteScreenWithAPI
import com.example.academically.uiAcademicAlly.institute.OrganizationsScreen
import com.example.academically.uiAcademicAlly.institute.TabletEventBlogScreen
import com.example.academically.uiAcademicAlly.schedule.AddScheduleActivityScreenWithViewModel
import com.example.academically.uiAcademicAlly.schedule.EditScheduleActivityScreen
import com.example.academically.uiAcademicAlly.schedule.ScheduleScreenWithViewModel
import com.example.academically.uiAcademicAlly.schedule.TabletScheduleScreen
import com.example.academically.uiAcademicAlly.settings.ConfigurationScreen
import com.example.academically.uiAcademicAlly.settings.LoginScreen
import com.example.academically.uiAcademicAlly.settings.LoginScreenWithViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationHost(
    navController: NavHostController,
    isTablet: Boolean = false
) {
    val context = LocalContext.current
    val database = AcademicAllyDatabase.getDatabase(context)

    // REPOSITORIOS
    val authRepository = AuthRepository(database.userProfileDao())
    val scheduleRepository = ScheduleRepository(database.scheduleDao())
    val eventRepository = PersonalEventRepository(database.personalEventDao())
    val apiService = remember { ApiService() }

    // VIEW MODELS
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository)
    )
    val scheduleViewModel: ScheduleViewModel = viewModel(
        factory = ScheduleViewModelFactory(scheduleRepository)
    )
    val eventViewModel: EventViewModel = viewModel(
        factory = EventViewModel.Factory(eventRepository)
    )
    val instituteViewModel: InstituteViewModel = viewModel()
    val blogEventsViewModel: BlogEventsViewModel = viewModel(
        factory = BlogEventsViewModel.Factory(apiService)
    )

    // ========== ESTADO DE AUTENTICACIÓN ==========
    val authUiState by authViewModel.authUiState.collectAsState()

    // Determinar la ruta inicial basada en el estado de autenticación
    val startDestination = when (authUiState) {
        is AuthUiState.Authenticated -> NavigationItemContent.Calendar.ruta
        is AuthUiState.Unauthenticated -> NavigationItemContent.Login.ruta
        is AuthUiState.Loading -> NavigationItemContent.Login.ruta // Mientras carga, mostrar login
        is AuthUiState.Error -> NavigationItemContent.Login.ruta
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ========== PANTALLAS DE AUTENTICACIÓN ==========
        composable(NavigationItemContent.Login.ruta) {
            LoginScreenWithViewModel(
                viewModel = authViewModel,
                onLoginSuccess = { userRole ->
                    // Navegar al calendario después del login exitoso
                    navController.navigate(NavigationItemContent.Calendar.ruta) {
                        // Limpiar el stack para que no pueda volver al login
                        popUpTo(NavigationItemContent.Login.ruta) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(NavigationItemContent.Register.ruta)
                }
            )
        }

        composable(NavigationItemContent.Register.ruta) {
            RegisterScreenWithViewModel(
                viewModel = authViewModel,
                onRegisterSuccess = { userRole ->
                    // Navegar al calendario después del registro exitoso
                    navController.navigate(NavigationItemContent.Calendar.ruta) {
                        // Limpiar el stack para que no pueda volver al registro
                        popUpTo(NavigationItemContent.Register.ruta) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigateUp()
                }
            )
        }

        // ========== PANTALLAS PRINCIPALES (REQUIEREN AUTENTICACIÓN) ==========
        composable(NavigationItemContent.Calendar.ruta) {
            // Verificar autenticación antes de mostrar la pantalla
            when (authUiState) {
                is AuthUiState.Authenticated -> {
                    if (isTablet) {
                        TabletCalendarScreen()
                    } else {
                        CalendarScreenWithViewModel(
                            viewModel = eventViewModel,
                            onAddEventClick = {
                                navController.navigate(NavigationItemContent.AddEvent.ruta)
                            },
                            onEditEventClick = { event ->
                                navController.navigate("${NavigationItemContent.EditEvent.ruta}/${event.id}")
                            }
                        )
                    }
                }
                else -> {
                    // Si no está autenticado, redirigir al login
                    LaunchedEffect(Unit) {
                        navController.navigate(NavigationItemContent.Login.ruta) {
                            popUpTo(NavigationItemContent.Calendar.ruta) { inclusive = true }
                        }
                    }
                }
            }
        }

        composable(NavigationItemContent.AddEvent.ruta) {
            when (authUiState) {
                is AuthUiState.Authenticated -> {
                    AddEventScreenWithViewModel(
                        viewModel = eventViewModel,
                        onBack = { navController.navigateUp() }
                    )
                }
                else -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(NavigationItemContent.Login.ruta) {
                            popUpTo(NavigationItemContent.AddEvent.ruta) { inclusive = true }
                        }
                    }
                }
            }
        }

        composable("${NavigationItemContent.EditEvent.ruta}/{eventId}") { backStackEntry ->
            when (authUiState) {
                is AuthUiState.Authenticated -> {
                    val eventId = backStackEntry.arguments?.getString("eventId")?.toIntOrNull()
                    if (eventId != null) {
                        EditEventScreenWithViewModel(
                            eventId = eventId,
                            viewModel = eventViewModel,
                            onBack = { navController.navigateUp() }
                        )
                    } else {
                        navController.navigateUp()
                    }
                }
                else -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(NavigationItemContent.Login.ruta) {
                            popUpTo("${NavigationItemContent.EditEvent.ruta}/{eventId}") { inclusive = true }
                        }
                    }
                }
            }
        }

        composable(NavigationItemContent.Settings.ruta) {
            when (authUiState) {
                is AuthUiState.Authenticated -> {
                    ConfigurationScreen(
                        OnClicKRes = {
                            // Cerrar sesión
                            authViewModel.logout()
                            navController.navigate(NavigationItemContent.Login.ruta) {
                                // Limpiar todo el stack de navegación
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
                else -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(NavigationItemContent.Login.ruta) {
                            popUpTo(NavigationItemContent.Settings.ruta) { inclusive = true }
                        }
                    }
                }
            }
        }

        composable(NavigationItemContent.Institute.ruta) {
            when (authUiState) {
                is AuthUiState.Authenticated -> {
                    OrganizationsScreen(
                        onOrganizationClick = {
                            navController.navigate(NavigationItemContent.BlogOrganization.ruta)
                        },
                        onAddOrganizationClick = {
                            navController.navigate(NavigationItemContent.AddOrganization.ruta)
                        }
                    )
                }
                else -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(NavigationItemContent.Login.ruta) {
                            popUpTo(NavigationItemContent.Institute.ruta) { inclusive = true }
                        }
                    }
                }
            }
        }

        composable(NavigationItemContent.AddOrganization.ruta) {
            when (authUiState) {
                is AuthUiState.Authenticated -> {
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
                }
                else -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(NavigationItemContent.Login.ruta) {
                            popUpTo(NavigationItemContent.AddOrganization.ruta) { inclusive = true }
                        }
                    }
                }
            }
        }

        composable(NavigationItemContent.BlogOrganization.ruta) {
            when (authUiState) {
                is AuthUiState.Authenticated -> {
                    if (isTablet) {
                        TabletEventBlogScreen(
                            events = BlogDataExample.getSampleBlog(),
                            eventViewModel = eventViewModel
                        )
                    } else {
                        EventBlogScreen(
                            blogEventsViewModel = blogEventsViewModel,
                            eventViewModel = eventViewModel
                        )
                    }
                }
                else -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(NavigationItemContent.Login.ruta) {
                            popUpTo(NavigationItemContent.BlogOrganization.ruta) { inclusive = true }
                        }
                    }
                }
            }
        }

        composable(NavigationItemContent.Schedule.ruta) {
            when (authUiState) {
                is AuthUiState.Authenticated -> {
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
                else -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(NavigationItemContent.Login.ruta) {
                            popUpTo(NavigationItemContent.Schedule.ruta) { inclusive = true }
                        }
                    }
                }
            }
        }

        composable("${NavigationItemContent.EditEventSchedule.ruta}/{scheduleId}") { backStackEntry ->
            when (authUiState) {
                is AuthUiState.Authenticated -> {
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
                else -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(NavigationItemContent.Login.ruta) {
                            popUpTo("${NavigationItemContent.EditEventSchedule.ruta}/{scheduleId}") { inclusive = true }
                        }
                    }
                }
            }
        }

        composable(NavigationItemContent.AddEventSchedule.ruta) {
            when (authUiState) {
                is AuthUiState.Authenticated -> {
                    AddScheduleActivityScreenWithViewModel(
                        viewModel = scheduleViewModel,
                        onNavigateBack = { navController.navigateUp() }
                    )
                }
                else -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(NavigationItemContent.Login.ruta) {
                            popUpTo(NavigationItemContent.AddEventSchedule.ruta) { inclusive = true }
                        }
                    }
                }
            }
        }
    }

    // ========== OBSERVAR CAMBIOS EN AUTENTICACIÓN ==========
    LaunchedEffect(authUiState) {
        when (authUiState) {
            is AuthUiState.Unauthenticated -> {
                // Si el usuario se desconecta, navegar al login
                navController.navigate(NavigationItemContent.Login.ruta) {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AuthUiState.Error -> {
                // En caso de error, también navegar al login
                navController.navigate(NavigationItemContent.Login.ruta) {
                    popUpTo(0) { inclusive = true }
                }
            }
            else -> { /* No hacer nada para Loading y Authenticated */ }
        }
    }
}