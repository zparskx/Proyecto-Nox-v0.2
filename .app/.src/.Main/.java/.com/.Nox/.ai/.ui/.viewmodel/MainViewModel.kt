package com.nox.ai.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nox.ai.data.local.AiPersona
import com.nox.ai.data.local.AppDatabase
import com.nox.ai.data.local.BenchmarkTest
import com.nox.ai.data.local.ChatMessage
import com.nox.ai.data.local.TrainingData
import com.nox.ai.data.presets.PresetTemplate
import com.nox.ai.data.presets.PresetTemplates
import com.nox.ai.data.repository.AiStudioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AiStudioRepository

    init {
        val dao = AppDatabase.getDatabase(application).aiStudioDao()
        repository = AiStudioRepository(dao, com.nox.ai.data.local.LocalNoxEngine(application.applicationContext))

        // Seed preset templates if DB is completely empty
        viewModelScope.launch {
            repository.allPersonas.collect { list ->
                if (list.isEmpty()) {
                    seedDefaultPresets()
                }
            }
        }
    }

    val personas: StateFlow<List<AiPersona>> = repository.allPersonas.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allGlobalTrainingData: StateFlow<List<TrainingData>> = repository.getAllTrainingData().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _selectedPersonaId = MutableStateFlow<Long?>(null)
    val selectedPersonaId: StateFlow<Long?> = _selectedPersonaId.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val selectedPersona: StateFlow<AiPersona?> = _selectedPersonaId.flatMapLatest { id ->
        if (id != null) repository.getPersonaFlow(id) else flowOf(null)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val trainingDataList: StateFlow<List<TrainingData>> = _selectedPersonaId.flatMapLatest { id ->
        if (id != null) repository.getTrainingDataForPersona(id) else flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val chatMessages: StateFlow<List<ChatMessage>> = _selectedPersonaId.flatMapLatest { id ->
        if (id != null) repository.getChatMessagesForPersona(id) else flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val benchmarkTests: StateFlow<List<BenchmarkTest>> = _selectedPersonaId.flatMapLatest { id ->
        if (id != null) repository.getBenchmarkTestsForPersona(id) else flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // UI States
    private val _isGeneratingResponse = MutableStateFlow(false)
    val isGeneratingResponse: StateFlow<Boolean> = _isGeneratingResponse.asStateFlow()

    private val _isSynthesizingDataset = MutableStateFlow(false)
    val isSynthesizingDataset: StateFlow<Boolean> = _isSynthesizingDataset.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun selectPersona(id: Long?) {
        _selectedPersonaId.value = id
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // Persona Actions
    fun createPersona(
        name: String,
        description: String,
        category: String,
        systemInstruction: String,
        baseModel: String,
        temperature: Float,
        topP: Float,
        iconName: String,
        colorHex: String,
        onCreated: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val newPersona = AiPersona(
                name = name,
                description = description,
                category = category,
                systemInstruction = systemInstruction,
                baseModel = baseModel,
                temperature = temperature,
                topP = topP,
                iconName = iconName,
                colorHex = colorHex
            )
            val newId = repository.insertPersona(newPersona)
            _selectedPersonaId.value = newId
            onCreated(newId)
        }
    }

    fun updatePersona(persona: AiPersona) {
        viewModelScope.launch {
            repository.updatePersona(persona)
        }
    }

    fun deletePersona(persona: AiPersona) {
        viewModelScope.launch {
            if (_selectedPersonaId.value == persona.id) {
                _selectedPersonaId.value = null
            }
            repository.deletePersona(persona)
        }
    }

    // Clone Preset Template
    fun clonePresetTemplate(template: PresetTemplate, onCloned: (Long) -> Unit) {
        viewModelScope.launch {
            val personaId = repository.insertPersona(template.persona.copy(id = 0))
            template.initialDataset.forEach { data ->
                repository.insertTrainingData(data.copy(id = 0, personaId = personaId))
            }
            _selectedPersonaId.value = personaId
            onCloned(personaId)
        }
    }

    // Training Data Actions
    fun addTrainingData(
        personaId: Long,
        title: String,
        contentType: String,
        content: String
    ) {
        viewModelScope.launch {
            val tokenEst = (content.length / 4).coerceAtLeast(10)
            val item = TrainingData(
                personaId = personaId,
                title = title,
                contentType = contentType,
                content = content,
                tokenEstimate = tokenEst
            )
            repository.insertTrainingData(item)
        }
    }

    fun toggleTrainingDataActive(data: TrainingData) {
        viewModelScope.launch {
            repository.updateTrainingData(data.copy(isActive = !data.isActive))
        }
    }

    fun deleteTrainingData(data: TrainingData) {
        viewModelScope.launch {
            repository.deleteTrainingData(data)
        }
    }

    // AI Dataset Synthesizer
    fun synthesizeDataset(
        rawText: String,
        contentType: String,
        title: String,
        personaId: Long,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            _isSynthesizingDataset.value = true
            val result = repository.synthesizeTrainingDataLocally(rawText, contentType)
            _isSynthesizingDataset.value = false

            result.fold(
                onSuccess = { synthesizedText ->
                    addTrainingData(
                        personaId = personaId,
                        title = if (title.isBlank()) "Sintetizado por IA" else title,
                        contentType = contentType,
                        content = synthesizedText
                    )
                    onComplete()
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Error al sintetizar datos de entrenamiento."
                }
            )
        }
    }

    // Chat Actions
    fun sendMessage(userText: String) {
        val currentPersona = selectedPersona.value ?: return
        if (userText.isBlank()) return

        viewModelScope.launch {
            // Save User message
            val userMsg = ChatMessage(
                personaId = currentPersona.id,
                sender = "USER",
                text = userText
            )
            repository.insertChatMessage(userMsg)

            _isGeneratingResponse.value = true
            val activeData = repository.getActiveTrainingDataList(currentPersona.id)

            val result = repository.sendPromptToNox(
                persona = currentPersona,
                userPrompt = userText,
                history = chatMessages.value
            )
            _isGeneratingResponse.value = false

            result.fold(
                onSuccess = { aiReply ->
                    val aiMsg = ChatMessage(
                        personaId = currentPersona.id,
                        sender = "AI",
                        text = aiReply,
                        isGroundedWithData = activeData.isNotEmpty(),
                        activeDatasetsCount = activeData.size
                    )
                    repository.insertChatMessage(aiMsg)
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                    val errorMsg = ChatMessage(
                        personaId = currentPersona.id,
                        sender = "AI",
                        text = "⚠️ ERROR: ${error.localizedMessage ?: "No se pudo ejecutar el modelo local de Nox. Instala un modelo .task primero."}",
                        isError = true
                    )
                    repository.insertChatMessage(errorMsg)
                }
            )
        }
    }

    fun clearChatHistory() {
        val personaId = selectedPersonaId.value ?: return
        viewModelScope.launch {
            repository.clearChatMessagesForPersona(personaId)
        }
    }

    // Benchmark Tests
    fun addBenchmarkTest(prompt: String, expectedKeywords: String) {
        val personaId = selectedPersonaId.value ?: return
        viewModelScope.launch {
            val test = BenchmarkTest(
                personaId = personaId,
                prompt = prompt,
                expectedKeywords = expectedKeywords
            )
            repository.insertBenchmarkTest(test)
        }
    }

    fun runBenchmarkTest(test: BenchmarkTest) {
        val currentPersona = selectedPersona.value ?: return
        viewModelScope.launch {
            _isGeneratingResponse.value = true
            val result = repository.sendPromptToNox(
                persona = currentPersona,
                userPrompt = test.prompt
            )
            _isGeneratingResponse.value = false

            result.fold(
                onSuccess = { reply ->
                    repository.updateBenchmarkTest(
                        test.copy(
                            actualResponse = reply,
                            testedAt = System.currentTimeMillis()
                        )
                    )
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )
        }
    }

    fun rateBenchmarkTest(test: BenchmarkTest, score: Int) {
        viewModelScope.launch {
            repository.updateBenchmarkTest(test.copy(ratingScore = score))
        }
    }

    fun deleteBenchmarkTest(test: BenchmarkTest) {
        viewModelScope.launch {
            repository.deleteBenchmarkTest(test)
        }
    }

    private suspend fun seedDefaultPresets() {
        PresetTemplates.list.forEach { preset ->
            val id = repository.insertPersona(preset.persona)
            preset.initialDataset.forEach { data ->
                repository.insertTrainingData(data.copy(personaId = id))
            }
        }
    }
}
