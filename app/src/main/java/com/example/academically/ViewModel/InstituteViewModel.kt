package com.example.academically.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.academically.data.api.ApiService
import com.example.academically.data.api.Institute
import kotlinx.coroutines.launch

class InstituteViewModel : ViewModel() {

    private val apiService = ApiService()

    var institutes by mutableStateOf<List<Institute>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var searchQuery by mutableStateOf("")
        private set

    init {
        loadInstitutes()
    }

    fun loadInstitutes() {
        viewModelScope.launch {
            isLoading = true
            error = null

            try {
                // Llamada simple sin Result wrapper
                val result = apiService.getAllInstitutes()
                val institutesList = result.getOrNull()

                if (institutesList != null) {
                    institutes = institutesList
                } else {
                    error = "No se pudieron cargar los institutos"
                    institutes = getFallbackInstitutes()
                }
            } catch (e: Exception) {
                error = "Error: ${e.message}"
                institutes = getFallbackInstitutes()
            }

            isLoading = false
        }
    }

    fun searchInstitutes(query: String) {
        searchQuery = query

        if (query.isBlank()) {
            loadInstitutes()
            return
        }

        // Búsqueda local por ahora (más simple)
        val allInstitutes = institutes
        institutes = allInstitutes.filter { institute ->
            institute.name.contains(query, ignoreCase = true) ||
                    institute.acronym.contains(query, ignoreCase = true)
        }
    }

    fun clearError() {
        error = null
    }

    private fun getFallbackInstitutes(): List<Institute> {
        return listOf(
            Institute(
                instituteID = 1,
                acronym = "ITP",
                name = "Instituto Tecnológico de Puebla",
                address = "Del Tecnológico 420, Puebla",
                email = "info@puebla.tecnm.mx",
                phone = "222 229 8810",
                studentNumber = 6284,
                teacherNumber = 298,
                webSite = "https://www.puebla.tecnm.mx"
            )
        )
    }
}