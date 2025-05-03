package com.example.academically.uiAcademicAlly.institute

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.academically.data.Career
import com.example.academically.data.Institute
import com.example.academically.data.SampleInstituteData.getSampleInstitutes


@Composable
fun InstituteAndCareerSelectionFlow(
    institutes: List<Institute>,
    onInstituteAndCareerSelected: (Institute, Career) -> Unit
) {
    // Estados para controlar el flujo de selección
    var selectedInstitute by remember { mutableStateOf<Institute?>(null) }
    var showCareerSelection by remember { mutableStateOf(false) }

    // Pantalla principal con la lista de institutos
    InstituteScreen(
        institutes = institutes,
        onInstituteAdded = { institute ->
            selectedInstitute = institute
            showCareerSelection = true
        }
    )

    // Mostrar diálogo de selección de carrera si corresponde
    if (showCareerSelection && selectedInstitute != null) {
        CareerSelectionDialogImproved(
            institute = selectedInstitute!!,
            onDismiss = { showCareerSelection = false },
            onCareerSelected = { career ->
                // Notificar que se ha seleccionado un instituto y una carrera
                onInstituteAndCareerSelected(selectedInstitute!!, career)
                showCareerSelection = false
            }
        )
    }
}


@Composable
fun InstituteScreen(
    institutes: List<Institute>,
    onInstituteAdded: (Institute) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedInstitute by remember { mutableStateOf<Institute?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Barra de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Buscar Organización") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            shape = RoundedCornerShape(4.dp),
            singleLine = true
        )

        // Grid de institutos
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Filtrar por búsqueda si hay un término
            val filteredInstitutes = if (searchQuery.isBlank()) {
                institutes
            } else {
                institutes.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                            it.acronym.contains(searchQuery, ignoreCase = true)
                }
            }

            items(filteredInstitutes) { institute ->
                InstituteButton(
                    institute = institute,
                    onClick = { selectedInstitute = institute }
                )
            }

        }
    }

    // Mostrar diálogo de detalle de instituto si está seleccionado
    selectedInstitute?.let { institute ->
        InstituteDetailDialog(
            institute = institute,
            onDismiss = { selectedInstitute = null },
            onAddInstitute = {
                onInstituteAdded(institute)
                selectedInstitute = null
            }
        )
    }
}


@Composable
fun InstituteButton(
    institute: Institute,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Logo del instituto
            InstituteLogoImage(institute)

            Spacer(modifier = Modifier.width(8.dp))

            // Acrónimo
            Text(
                text = institute.acronym,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun InstituteLogoImage(institute: Institute) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        if (institute.logo != null) {
           Image(painter = painterResource(id = institute.logo!!), contentDescription = institute.name,
               modifier = Modifier.size(24.dp),)
        } else {
            // Icono por defecto
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = "Instituto",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
fun InstituteDetailDialog(
    institute: Institute,
    onDismiss: () -> Unit,
    onAddInstitute: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

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
                // Logo y nombre
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (institute.logo != null) {
                        /*Icon(
                            imageVector = institute.logo!!,
                            contentDescription = institute.name,
                            modifier = Modifier.size(40.dp),
                            tint = Color.Unspecified
                        )*/
                    } else {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Instituto",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = institute.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Información del instituto
                InstituteDetailItem("Dirección:", institute.address)
                InstituteDetailItem("Teléfono:", institute.phone)
                InstituteDetailItem("Alumnos:", institute.studentNumber.toString())
                InstituteDetailItem("Docentes:", institute.teacherNumber.toString())

                // Sitio web
                institute.webSite?.let { website ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sitio Web:",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(100.dp)
                        )
                        TextButton(
                            onClick = {
                                try {
                                    uriHandler.openUri(website)
                                } catch (e: Exception) {
                                    // Manejar error si la URL no es válida
                                }
                            }
                        ) {
                            Text(
                                text = website,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Redes sociales
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Aquí se pueden agregar iconos de redes sociales como botones
                    // Por simplicidad, usaremos placeholders
                    if (institute.facebook != null) {
                        IconButton( onClick = {uriHandler.openUri(
                            institute.facebook!!

                        )}) {
                            Icon(imageVector = Icons.Default.Facebook, contentDescription = "Facebook")
                        }
                    }
                    if (institute.instagram != null) {
                        IconButton( onClick = {uriHandler.openUri(
                            institute.instagram!!

                        )}) {
                            Icon(imageVector = Icons.Default.CameraEnhance, contentDescription = "Facebook")
                        }
                    }
                    if (institute.youtube != null) {
                        IconButton( onClick = {uriHandler.openUri(
                            institute.youtube!!

                        )}) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Facebook")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }

                    Button(onClick = onAddInstitute) {
                        Text("Agregar")
                    }
                }
            }
        }
    }
}

@Composable
fun InstituteDetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SocialMediaIcon(platform: String) {
    // En una implementación real, se usarían iconos reales
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .padding(8.dp)
            .clickable { /* Abrir red social */ },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = platform.first().toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
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

@Preview
@Composable
fun ExampleSelectionCareer(){
    CareerSelectionDialogImproved(
        institute = getSampleInstitutes()[0],
        onDismiss = { /*TODO*/ },
        onCareerSelected = { /*TODO*/ }
    )
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
}@Composable
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


@Preview(showSystemUi = true)
@Composable
fun FinalIntegratedFlowPreview() {
    // Obtener datos de muestra
    val availableInstitutes = getSampleInstitutes()

    // Lista mutable para institutos y carreras seleccionados
    val selectedInstitutesWithCareers = remember {
        mutableStateListOf<Pair<Institute, Career>>()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        color = MaterialTheme.colorScheme.background
    ) {
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
}