package com.example.academically

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.academically.ui.theme.AcademicAllyTheme
import com.example.academically.uiAcademicAlly.NavigationHost
import com.example.academically.uiAcademicAlly.NavigationItemContent
import com.example.academically.utils.currentRoute

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AcademicAllyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { NavegacionInferior(navController = navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            NavigationHost(navController = navController)
        }
    }
}

@Composable
fun NavegacionInferior(navController: NavHostController) {
    val menuItems = listOf(
        NavigationItemContent.Calendar,
        NavigationItemContent.Schedule,
        NavigationItemContent.Organization,
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
                    },
                    label = {
                        Text(item.text)
                    }
                )
            }
        }
    }
}