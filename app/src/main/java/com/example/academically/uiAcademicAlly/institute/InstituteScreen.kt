package com.example.academically.uiAcademicAlly.institute

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.academically.ViewModel.InstituteViewModel
import com.example.academically.data.Career
import com.example.academically.data.Institute
import com.example.academically.data.api.Institute as ApiInstitute

@Composable
fun InstituteScreenWithAPI(
    viewModel: InstituteViewModel,
    onInstituteAndCareerSelected: (Institute, Career) -> Unit
) {
    // Estados para controlar el flujo de selección
    var selectedInstitute by remember { mutableStateOf<Institute?>(null) }
    var showCareerSelection by remember { mutableStateOf(false) }

    // Pantalla principal
    InstituteSearchScreenWithAPI(
        viewModel = viewModel,
        onInstituteSelected = { apiInstitute ->
            val localInstitute = apiInstitute.toLocalInstitute()
            selectedInstitute = localInstitute
            showCareerSelection = true
        }
    )

    // Mostrar diálogo de selección de carrera si corresponde
    if (showCareerSelection && selectedInstitute != null) {
        CareerSelectionDialogImproved(
            institute = selectedInstitute!!,
            onDismiss = {
                showCareerSelection = false
                selectedInstitute = null
            },
            onCareerSelected = { career ->
                onInstituteAndCareerSelected(selectedInstitute!!, career)
                showCareerSelection = false
                selectedInstitute = null
            }
        )
    }
}

@Composable
fun CareerSelectionDialogImproved(
    institute: Institute,
    onDismiss: () -> Unit,
    onCareerSelected: (Career) -> Unit
) {
    // Las carreras ahora vienen directamente del instituto
    val careers = institute.listCareer

    // Estado para la carrera seleccionada
    var selectedCareer by remember { mutableStateOf<Career?>(null) }

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
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Selecciona tu carrera",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (careers.isEmpty()) {
                    // Mostrar mensaje si no hay carreras disponibles
                    NoCareerAvailableMessage()
                } else {
                    // Componente mejorado de dropdown
                    CareerDropdown(
                        careers = careers,
                        selectedCareer = selectedCareer,
                        onCareerSelected = { selectedCareer = it }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            selectedCareer?.let { onCareerSelected(it) }
                        },
                        enabled = selectedCareer != null
                    ) {
                        Text("Agregar")
                    }
                }
            }
        }
    }
}


@Composable
fun CareerDropdown(
    careers: List<Career>,
    selectedCareer: Career?,
    onCareerSelected: (Career) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldBounds by remember { mutableStateOf<androidx.compose.ui.geometry.Rect?>(null) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selectedCareer?.name ?: "",
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldBounds = coordinates.boundsInWindow()
                },
            readOnly = true,
            label = { Text("Selecciona una carrera") },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Contraer" else "Expandir",
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }
        )

        // Área invisible para hacer clic que expande/contrae el dropdown
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = !expanded }
        )

        // Popup personalizado que se despliega desde arriba
        if (expanded && careers.isNotEmpty()) {
            Popup(
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .heightIn(max = 300.dp)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    LazyColumn {
                        items(careers) { career ->
                            DropdownMenuItem(
                                text = { Text(career.name) },
                                onClick = {
                                    onCareerSelected(career)
                                    expanded = false
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = if (career.careerID == selectedCareer?.careerID)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun NoCareerAvailableMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Advertencia",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "No hay carreras disponibles para este instituto",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Por favor, selecciona otro instituto o contacta al administrador",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.alpha(0.7f)
        )
    }
}


@Composable
fun InstituteSearchScreenWithAPI(
    viewModel: InstituteViewModel,
    onInstituteSelected: (ApiInstitute) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 28.dp, horizontal = 16.dp)
    ) {
        // Mostrar error si existe
        viewModel.error?.let { errorMessage ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚠️ $errorMessage",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("Cerrar")
                    }
                }
            }
        }

        // Barra de búsqueda
        OutlinedTextField(
            value = viewModel.searchQuery,
            onValueChange = { viewModel.searchInstitutes(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Buscar Instituto o Universidad") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            shape = RoundedCornerShape(4.dp),
            singleLine = true
        )

        // Indicador de carga
        if (viewModel.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Cargando institutos...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Grid de institutos
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(viewModel.institutes) { institute ->
                InstituteCardAPI(
                    institute = institute,
                    onClick = { onInstituteSelected(institute) }
                )
            }
        }

        // Información y botón de recarga
        if (!viewModel.isLoading) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (viewModel.institutes.isEmpty() && viewModel.searchQuery.isNotBlank()) {
                    Text(
                        text = "No se encontraron institutos para '${viewModel.searchQuery}'",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                TextButton(
                    onClick = { viewModel.loadInstitutes() }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Recargar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Recargar desde servidor")
                }

                // Mostrar total de institutos
                Text(
                    text = "${viewModel.institutes.size} institutos disponibles",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun InstituteCardAPI(
    institute: ApiInstitute,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Acrónimo
            Text(
                text = institute.acronym,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Nombre completo (truncado)
            Text(
                text = institute.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                fontSize = 10.sp
            )

            // Número de estudiantes
            Text(
                text = "${institute.studentNumber} estudiantes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 9.sp
            )
        }
    }
}

// Función de conversión (reutilizada)
fun ApiInstitute.toLocalInstitute(): Institute {
    return Institute(
        instituteID = this.instituteID,
        acronym = this.acronym,
        name = this.name,
        address = this.address,
        email = this.email,
        phone = this.phone,
        studentNumber = this.studentNumber,
        teacherNumber = this.teacherNumber,
        webSite = this.webSite,
        facebook = this.facebook,
        instagram = this.instagram,
        twitter = this.twitter,
        youtube = this.youtube,
        listCareer = this.listCareer.map { apiCareer ->
            Career(
                careerID = apiCareer.careerID,
                name = apiCareer.name,
                acronym = apiCareer.acronym,
                email = apiCareer.email,
                phone = apiCareer.phone
            )
        }
    )
}