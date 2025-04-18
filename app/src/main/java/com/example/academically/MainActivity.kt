package com.example.academically

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.academically.data.Event
import com.example.academically.data.EventProcessor
import com.example.academically.data.EventShape
import com.example.academically.data.ProcessedEvent
import com.example.academically.data.SystemCalendarProvider
import com.example.academically.ui.theme.AcademicAllyTheme
import com.example.academically.uiAcademicAlly.CalendarAppScreen
import com.example.academically.uiAcademicAlly.CalendarPreview
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AcademicAllyTheme {
                Box(modifier = Modifier.fillMaxSize()){
                    CalendarPreview()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

