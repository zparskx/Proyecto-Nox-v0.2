package com.nox.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nox.ai.data.local.AiPersona
import com.nox.ai.data.local.BenchmarkTest
import com.nox.ai.data.local.TrainingData
import com.nox.ai.ui.components.ApiKeyWarningBanner
import com.nox.ai.ui.components.EmptyStateCard
import com.nox.ai.ui.components.GoogleAiNetworkBadge
import com.nox.ai.ui.components.ParameterSlider
import com.nox.ai.ui.components.getColorForHex
import com.nox.ai.ui.components.getIconForName
import com.nox.ai.ui.theme.CyanAccent
import com.nox.ai.ui.theme.EmeraldGlow
import com.nox.ai.ui.theme.GoogleBlue
import com.nox.ai.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonaStudioScreen(
    personaId: Long?,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToChat: (Long) -> Unit
) {
    val selectedPersona by viewModel.selectedPersona.collectAsState()
    val trainingDataList by viewModel.trainingDataList.collectAsState()
    val benchmarkTests by viewModel.benchmarkTests.collectAsState()
    val isGenerating by viewModel.isGeneratingResponse.collectAsState()
    val isSynthesizing by viewModel.isSynthesizingDataset.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(personaId) {
        viewModel.selectPersona(personaId)
    }

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("1. Identidad & Modelo", "2. Datos de Entrenamiento", "3. Arena de Pruebas")

    // State fields for editing identity
    var name by remember(selectedPersona) { mutableStateOf(selectedPersona?.name ?: "Mi IA Personalizada") }
    var description by remember(selectedPersona) { mutableStateOf(selectedPersona?.description ?: "Entrenada para responder preguntas específicas.") }
    var category by remember(selectedPersona) { mutableStateOf(selectedPersona?.category ?: "General") }
    var systemInstruction by remember(selectedPersona) { mutableStateOf(selectedPersona?.systemInstruction ?: "Eres un asistente de IA personalizado. Responde de forma precisa y clara.") }
    var baseModel by remember(selectedPersona) { mutableStateOf(selectedPersona?.baseModel ?: "gemini-3.5-flash") }
    var temperature by remember(selectedPersona) { mutableFloatStateOf(selectedPersona?.temperature ?: 0.7f) }
    var topP by remember(selectedPersona) { mutableFloatStateOf(selectedPersona?.topP ?: 0.95f) }
    var selectedIconName by remember(selectedPersona) { mutableStateOf(selectedPersona?.iconName ?: "psychology") }
    var selectedColorHex by remember(selectedPersona) { mutableStateOf(selectedPersona?.colorHex ?: "#4285F4") }

    var showAddDatasetDialog by remember { mutableStateOf(false) }
    var showAiSynthesizerDialog by remember { mutableStateOf(false) }
    var showAddBenchmarkDialog by remember { mutableStateOf(false) }

    val iconsList = listOf("psychology", "code", "school", "payments", "fitness_center", "headset_mic")
    val colorsList = listOf("#4285F4", "#10B981", "#8A2BE2", "#FBBC05", "#EA4335", "#00E5FF")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (personaId == null) "Crear Nueva IA" else "Estudio de Entrenamiento",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (selectedPersona != null) selectedPersona!!.name else "Configuración inicial de modelo",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (personaId != null) {
                        Button(
                            onClick = { onNavigateToChat(personaId) },
                            colors = ButtonDefaults.buttonColors(containerColor = GoogleBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Chat", fontSize = 12.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ApiKeyWarningBanner(errorMessage = errorMessage, onDismissError = { viewModel.clearError() })

            // Studio Tab Navigation
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = GoogleBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = GoogleBlue
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTabIndex == index) GoogleBlue else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> IdentityTabContent(
                    name = name,
                    onNameChange = { name = it },
                    description = description,
                    onDescriptionChange = { description = it },
                    category = category,
                    onCategoryChange = { category = it },
                    systemInstruction = systemInstruction,
                    onSystemInstructionChange = { systemInstruction = it },
                    baseModel = baseModel,
                    onBaseModelChange = { baseModel = it },
                    temperature = temperature,
                    onTemperatureChange = { temperature = it },
                    topP = topP,
                    onTopPChange = { topP = it },
                    selectedIconName = selectedIconName,
                    onIconChange = { selectedIconName = it },
                    selectedColorHex = selectedColorHex,
                    onColorChange = { selectedColorHex = it },
                    iconsList = iconsList,
                    colorsList = colorsList,
                    onSave = {
                        if (personaId == null) {
                            viewModel.createPersona(
                                name = name,
                                description = description,
                                category = category,
                                systemInstruction = systemInstruction,
                                baseModel = baseModel,
                                temperature = temperature,
                                topP = topP,
                                iconName = selectedIconName,
                                colorHex = selectedColorHex,
                                onCreated = { newId ->
                                    selectedTabIndex = 1 // Move to training datasets
                                }
                            )
                        } else {
                            selectedPersona?.let { current ->
                                viewModel.updatePersona(
                                    current.copy(
                                        name = name,
                                        description = description,
                                        category = category,
                                        systemInstruction = systemInstruction,
                                        baseModel = baseModel,
                                        temperature = temperature,
                                        topP = topP,
                                        iconName = selectedIconName,
                                        colorHex = selectedColorHex,
                                        updatedAt = System.currentTimeMillis()
                                    )
                                )
                                selectedTabIndex = 1
                            }
                        }
                    }
                )

                1 -> TrainingDataTabContent(
                    personaId = personaId,
                    trainingDataList = trainingDataList,
                    isSynthesizing = isSynthesizing,
                    onAddDatasetClick = { showAddDatasetDialog = true },
                    onAiSynthesizerClick = { showAiSynthesizerDialog = true },
                    onToggleActive = { viewModel.toggleTrainingDataActive(it) },
                    onDelete = { viewModel.deleteTrainingData(it) }
                )

                2 -> BenchmarkTabContent(
                    personaId = personaId,
                    benchmarkTests = benchmarkTests,
                    isGenerating = isGenerating,
                    onAddTestClick = { showAddBenchmarkDialog = true },
                    onRunTest = { viewModel.runBenchmarkTest(it) },
                    onRateTest = { test, score -> viewModel.rateBenchmarkTest(test, score) },
                    onDeleteTest = { viewModel.deleteBenchmarkTest(it) }
                )
            }
        }
    }

    // Dialogs
    if (showAddDatasetDialog && personaId != null) {
        AddDatasetDialog(
            onDismiss = { showAddDatasetDialog = false },
            onConfirm = { title, type, content ->
                viewModel.addTrainingData(personaId, title, type, content)
                showAddDatasetDialog = false
            }
        )
    }

    if (showAiSynthesizerDialog && personaId != null) {
        AiDatasetSynthesizerDialog(
            isSynthesizing = isSynthesizing,
            onDismiss = { showAiSynthesizerDialog = false },
            onConfirm = { rawText, contentType, title ->
                viewModel.synthesizeDataset(rawText, contentType, title, personaId) {
                    showAiSynthesizerDialog = false
                }
            }
        )
    }

    if (showAddBenchmarkDialog && personaId != null) {
        AddBenchmarkDialog(
            onDismiss = { showAddBenchmarkDialog = false },
            onConfirm = { prompt, expected ->
                viewModel.addBenchmarkTest(prompt, expected)
                showAddBenchmarkDialog = false
            }
        )
    }
}

// TAB 1: IDENTIDAD
@Composable
fun IdentityTabContent(
    name: String,
    onNameChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    systemInstruction: String,
    onSystemInstructionChange: (String) -> Unit,
    baseModel: String,
    onBaseModelChange: (String) -> Unit,
    temperature: Float,
    onTemperatureChange: (Float) -> Unit,
    topP: Float,
    onTopPChange: (Float) -> Unit,
    selectedIconName: String,
    onIconChange: (String) -> Unit,
    selectedColorHex: String,
    onColorChange: (String) -> Unit,
    iconsList: List<String>,
    colorsList: List<String>,
    onSave: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Información Básica de la IA", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    OutlinedTextField(
                        value = name,
                        onValueChange = onNameChange,
                        label = { Text("Nombre de la IA") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_name_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = onDescriptionChange,
                        label = { Text("Descripción Corta") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_desc_input")
                    )

                    OutlinedTextField(
                        value = category,
                        onValueChange = onCategoryChange,
                        label = { Text("Categoría (ej: Programación, Salud, Negocios)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Text(text = "Icono & Color de Tema", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(iconsList) { iconName ->
                            val icon = getIconForName(iconName)
                            val isSelected = selectedIconName == iconName
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) GoogleBlue else MaterialTheme.colorScheme.surface)
                                    .clickable { onIconChange(iconName) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(colorsList) { hex ->
                            val color = getColorForHex(hex)
                            val isSelected = selectedColorHex == hex
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(if (isSelected) 3.dp else 0.dp, Color.White, CircleShape)
                                    .clickable { onColorChange(hex) }
                            )
                        }
                    }
                }
            }
        }

        // Base Model Selection
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Selección de Modelo Base de Google AI", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (baseModel == "gemini-3.5-flash") GoogleBlue.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .clickable { onBaseModelChange("gemini-3.5-flash") }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = baseModel == "gemini-3.5-flash",
                            onClick = { onBaseModelChange("gemini-3.5-flash") },
                            colors = RadioButtonDefaults.colors(selectedColor = GoogleBlue)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Gemma 3 1B Local", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "Respuesta ultrarrápida, ideal para asistentes diarios y tutores.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (baseModel == "gemini-3.1-pro-preview") GoogleBlue.copy(alpha = 0.2f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .clickable { onBaseModelChange("gemini-3.1-pro-preview") }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = baseModel == "gemini-3.1-pro-preview",
                            onClick = { onBaseModelChange("gemini-3.1-pro-preview") },
                            colors = RadioButtonDefaults.colors(selectedColor = GoogleBlue)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Modelo Local Personalizado", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "Razonamiento avanzado, programación compleja y análisis profundo.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Hyperparameters Tuning
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Ajuste de Hiperparámetros (Tuning)", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    ParameterSlider(
                        title = "Temperatura (Creatividad vs Precisión)",
                        value = temperature,
                        range = 0f..1f,
                        unitText = "",
                        description = "0.0 = Respuestas estrictas y deterministas. 1.0 = Máxima creatividad.",
                        onValueChange = onTemperatureChange
                    )

                    ParameterSlider(
                        title = "Top P (Nucleus Sampling)",
                        value = topP,
                        range = 0f..1f,
                        unitText = "",
                        description = "Filtro probabilístico de vocabulario durante la generación.",
                        onValueChange = onTopPChange
                    )
                }
            }
        }

        // System Instruction / Prompt Engineering
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Instrucción de Sistema (Prompt Base)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(text = "~${systemInstruction.length / 4} tokens", fontSize = 11.sp, color = CyanAccent)
                    }
                    Text(
                        text = "Define la personalidad, reglas inviolables y tono de respuesta de tu IA.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = systemInstruction,
                        onValueChange = onSystemInstructionChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("system_instruction_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        item {
            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_identity_button"),
                colors = ButtonDefaults.buttonColors(containerColor = GoogleBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Guardar & Continuar a Entrenamiento", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// TAB 2: DATOS DE ENTRENAMIENTO
@Composable
fun TrainingDataTabContent(
    personaId: Long?,
    trainingDataList: List<TrainingData>,
    isSynthesizing: Boolean,
    onAddDatasetClick: () -> Unit,
    onAiSynthesizerClick: () -> Unit,
    onToggleActive: (TrainingData) -> Unit,
    onDelete: (TrainingData) -> Unit
) {
    if (personaId == null) {
        EmptyStateCard(
            title = "Guarda la Identidad de la IA primero",
            subtitle = "Guarda la configuración inicial en la pestaña 'Identidad & Modelo' antes de agregar datos de entrenamiento."
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Base de Conocimiento de Entrenamiento",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Agrega documentos, reglas o pares Pregunta/Respuesta para entrenar a tu IA. La red de Google AI usará esta información como contexto prioritario.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onAddDatasetClick,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("add_dataset_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = GoogleBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "+ Agregar Dato", fontSize = 12.sp)
                        }

                        Button(
                            onClick = onAiSynthesizerClick,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ai_synthesize_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent)
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Sintetizar con IA", fontSize = 12.sp, color = CyanAccent)
                        }
                    }
                }
            }
        }

        if (trainingDataList.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "Aún no hay datos de entrenamiento",
                    subtitle = "Toca '+ Agregar Dato' o usa 'Sintetizar con IA' para extraer pares de entrenamiento desde un texto."
                )
            }
        } else {
            items(trainingDataList) { data ->
                TrainingDataCardItem(
                    data = data,
                    onToggleActive = { onToggleActive(data) },
                    onDelete = { onDelete(data) }
                )
            }
        }
    }
}

@Composable
fun TrainingDataCardItem(
    data: TrainingData,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = GoogleBlue.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = data.contentType,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoogleBlue,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = data.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = data.isActive,
                        onCheckedChange = { onToggleActive() },
                        colors = SwitchDefaults.colors(checkedThumbColor = EmeraldGlow)
                    )
                    IconButton(onClick = onDelete) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = data.content,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 4
            )
        }
    }
}

// TAB 3: BENCHMARKS
@Composable
fun BenchmarkTabContent(
    personaId: Long?,
    benchmarkTests: List<BenchmarkTest>,
    isGenerating: Boolean,
    onAddTestClick: () -> Unit,
    onRunTest: (BenchmarkTest) -> Unit,
    onRateTest: (BenchmarkTest, Int) -> Unit,
    onDeleteTest: (BenchmarkTest) -> Unit
) {
    if (personaId == null) {
        EmptyStateCard(
            title = "Guarda la Identidad de la IA primero",
            subtitle = "Completa la configuración antes de ejecutar pruebas de rendimiento."
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Arena de Pruebas & Evaluación Benchmark", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        text = "Evalúa la precisión de tu modelo entrenado con prompts de prueba. Compara las respuestas generadas y califica el desempeño.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onAddTestClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_benchmark_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = GoogleBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "+ Agregar Prompt de Prueba Benchmark", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (benchmarkTests.isEmpty()) {
            item {
                EmptyStateCard(
                    title = "Sin pruebas de benchmark registradas",
                    subtitle = "Agrega prompts de prueba para validar que tu IA responde correctamente según sus datos de entrenamiento."
                )
            }
        } else {
            items(benchmarkTests) { test ->
                BenchmarkTestCardItem(
                    test = test,
                    isGenerating = isGenerating,
                    onRunTest = { onRunTest(test) },
                    onRateTest = { score -> onRateTest(test, score) },
                    onDeleteTest = { onDeleteTest(test) }
                )
            }
        }
    }
}

@Composable
fun BenchmarkTestCardItem(
    test: BenchmarkTest,
    isGenerating: Boolean,
    onRunTest: () -> Unit,
    onRateTest: (Int) -> Unit,
    onDeleteTest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Prompt: ${test.prompt}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                IconButton(onClick = onDeleteTest) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (test.expectedKeywords.isNotBlank()) {
                Text(text = "Palabras Clave Esperadas: ${test.expectedKeywords}", fontSize = 11.sp, color = CyanAccent)
            }

            if (test.actualResponse != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = "Respuesta Generada por Google AI:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoogleBlue)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = test.actualResponse, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Calificación:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(8.dp))
                    (1..5).forEach { star ->
                        val isSelected = (test.ratingScore ?: 0) >= star
                        Icon(
                            imageVector = if (isSelected) Icons.Default.Star else Icons.Default.StarOutline,
                            contentDescription = null,
                            tint = if (isSelected) GoogleBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(22.dp)
                                .clickable { onRateTest(star) }
                        )
                    }
                }
            }

            Button(
                onClick = onRunTest,
                enabled = !isGenerating,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GoogleBlue)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ejecutando en Google AI...")
                } else {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (test.actualResponse == null) "Ejecutar Prueba" else "Volver a Probar")
                }
            }
        }
    }
}

// DIALOGS
@Composable
fun AddDatasetDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, type: String, content: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("TEXT_DOC") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Dato de Entrenamiento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título del Documento") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dataset_title_input")
                )

                Text(text = "Tipo de Contenido", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("TEXT_DOC", "QA_PAIRS", "RULES_LIST").forEach { t ->
                        RadioButton(selected = type == t, onClick = { type = t })
                        Text(text = t, fontSize = 11.sp, modifier = Modifier.align(Alignment.CenterVertically))
                    }
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Contenido / Texto de Entrenamiento") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .testTag("dataset_content_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, type, content) },
                enabled = title.isNotBlank() && content.isNotBlank(),
                modifier = Modifier.testTag("confirm_dataset_button")
            ) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun AiDatasetSynthesizerDialog(
    isSynthesizing: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (rawText: String, type: String, title: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var rawText by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("QA_PAIRS") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = CyanAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sintetizador por IA")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Pega un texto en bruto (manual, notas, política) y Google AI extraerá datos estructurados de entrenamiento.", fontSize = 12.sp)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título de Referencia") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = rawText,
                    onValueChange = { rawText = it },
                    label = { Text("Texto en Bruto para Sintetizar") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(rawText, type, title) },
                enabled = !isSynthesizing && rawText.isNotBlank()
            ) {
                if (isSynthesizing) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                } else {
                    Text("Sintetizar & Guardar")
                }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun AddBenchmarkDialog(
    onDismiss: () -> Unit,
    onConfirm: (prompt: String, expected: String) -> Unit
) {
    var prompt by remember { mutableStateOf("") }
    var expected by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Prompt Benchmark") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    label = { Text("Prompt de Prueba") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = expected,
                    onValueChange = { expected = it },
                    label = { Text("Palabras Clave Esperadas en la Respuesta") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(prompt, expected) },
                enabled = prompt.isNotBlank()
            ) {
                Text("Agregar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
