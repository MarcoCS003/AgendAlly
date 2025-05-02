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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
            Surface(
                modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
            ) {
                AcademicAllyTheme {
                    MainScreen()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(bottomBar = { NavegacionInferior(navController = navController) }) { paddingValues ->
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
fun NavegacionInferior(
    navController: NavHostController
) {
    val menuItems = listOf(NavigationItemContent.Calendar,
        NavigationItemContent.Schedule, NavigationItemContent.Institute, NavigationItemContent.Next
    )

    BottomAppBar {
        NavigationBar {
            menuItems .forEach { item ->

                val selected = currentRoute(navController) == item.ruta
                NavigationBarItem(selected = false, onClick = { navController.navigate(item.ruta) }, icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.text
                    )
                })
            }
        }
    }
}
