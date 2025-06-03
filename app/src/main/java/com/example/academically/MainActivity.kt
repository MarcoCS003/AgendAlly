package com.example.academically

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.academically.ui.theme.AcademicAllyTheme
import com.example.academically.uiAcademicAlly.NavigationHost
import com.example.academically.uiAcademicAlly.NavigationItemContent
import com.example.academically.utils.currentRoute

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AcademicAllyTheme {
                    MainScreen(windowSizeClass = windowSizeClass)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()
    val isTablet = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Medium ||
            windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

    if (isTablet) {
        TabletLayout(navController = navController)
    } else {
        MobileLayout(navController = navController)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TabletLayout(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Navigation Rail mejorado
        AdaptiveNavigationRail(navController = navController)

        // Contenido principal
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(start = 8.dp)
        ) {
            NavigationHost(navController = navController, isTablet = true)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MobileLayout(navController: NavHostController) {
    Scaffold(
        bottomBar = { NavegacionInferior(navController = navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            NavigationHost(navController = navController, isTablet = false)
        }
    }
}

@Composable
fun AdaptiveNavigationRail(navController: NavHostController) {
    val menuItems = listOf(
        NavigationItemContent.Calendar,
        NavigationItemContent.Schedule,
        NavigationItemContent.Institute,
        NavigationItemContent.Settings
    )

    val currentRoute = currentRoute(navController)

    Card(
        modifier = Modifier
            .fillMaxHeight()
            .padding(start = 16.dp, top = 20.dp, bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme())
                Color(0xFF2D2D2D)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        NavigationRail(
            modifier = Modifier.padding(8.dp),
            containerColor = Color.Transparent
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            menuItems.forEach { item ->
                val selected = currentRoute == item.ruta

                NavigationRailItem(
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            navController.navigate(item.ruta) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            modifier = Modifier.size(36.dp),
                            contentDescription = item.text,
                            tint = if (selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                if (isSystemInDarkTheme()) Color.Gray else Color.DarkGray
                            }
                        )
                    },
                    label = {
                        Text(
                            item.text,
                            fontSize = 11.sp,
                            color = if (selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                if (isSystemInDarkTheme()) Color.Gray else Color.DarkGray
                            }
                        )
                    },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        unselectedIconColor = if (isSystemInDarkTheme()) Color.Gray else Color.DarkGray,
                        unselectedTextColor = if (isSystemInDarkTheme()) Color.Gray else Color.DarkGray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// Mantener la función original para móviles
@Composable
fun NavegacionInferior(navController: NavHostController) {
    val menuItems = listOf(
        NavigationItemContent.Calendar,
        NavigationItemContent.Schedule,
        NavigationItemContent.Institute,
        NavigationItemContent.Settings
    )

    val currentRoute = currentRoute(navController)

    BottomAppBar {
        NavigationBar {
            menuItems.forEach { item ->
                val selected = currentRoute == item.ruta

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            navController.navigate(item.ruta) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.text
                        )
                    }
                )
            }
        }
    }
}