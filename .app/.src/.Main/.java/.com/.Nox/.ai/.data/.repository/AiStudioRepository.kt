package com.nox.ai.data.repository

import com.nox.ai.data.local.AiPersona
import com.nox.ai.data.local.AiStudioDao
import com.nox.ai.data.local.BenchmarkTest
import com.nox.ai.data.local.ChatMessage
import com.nox.ai.data.local.TrainingData
import com.nox.ai.data.local.LocalNoxEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Local-first repository.
 *
 * The model never needs an API key. The LLM runs on-device through
 * Google's MediaPipe LLM Inference runtime.
 */
class AiStudioRepository(
    private val dao: AiStudioDao,
    private val noxEngine: LocalNoxEngine
) {
    val allPersonas: Flow<List<AiPersona>> = dao.getAllPersonas()

    fun getPersonaFlow(id: Long): Flow<AiPersona?> = dao.getPersonaFlowById(id)
    suspend fun getPersona(id: Long): AiPersona? = dao.getPersonaById(id)
    suspend fun insertPersona(persona: AiPersona): Long = dao.insertPersona(persona)
    suspend fun updatePersona(persona: AiPersona) = dao.updatePersona(persona)
    suspend fun deletePersona(persona: AiPersona) = dao.deletePersona(persona)

    fun getTrainingDataForPersona(personaId: Long): Flow<List<TrainingData>> =
        dao.getTrainingDataForPersona(personaId)

    suspend fun getActiveTrainingDataList(personaId: Long): List<TrainingData> =
        dao.getActiveTrainingDataList(personaId)

    fun getAllTrainingData(): Flow<List<TrainingData>> = dao.getAllTrainingData()

    suspend fun insertTrainingData(data: TrainingData): Long {
        val id = dao.insertTrainingData(data)
        dao.getPersonaById(data.personaId)?.let { persona ->
            dao.updatePersona(
                persona.copy(isTrained = true, updatedAt = System.currentTimeMillis())
            )
        }
        return id
    }

    suspend fun updateTrainingData(data: TrainingData) = dao.updateTrainingData(data)
    suspend fun deleteTrainingData(data: TrainingData) = dao.deleteTrainingData(data)

    fun getChatMessagesForPersona(personaId: Long): Flow<List<ChatMessage>> =
        dao.getChatMessagesForPersona(personaId)

    suspend fun insertChatMessage(message: ChatMessage): Long = dao.insertChatMessage(message)
    suspend fun clearChatMessagesForPersona(personaId: Long) =
        dao.clearChatMessagesForPersona(personaId)

    fun getBenchmarkTestsForPersona(personaId: Long): Flow<List<BenchmarkTest>> =
        dao.getBenchmarkTestsForPersona(personaId)

    suspend fun insertBenchmarkTest(test: BenchmarkTest): Long =
        dao.insertBenchmarkTest(test)

    suspend fun updateBenchmarkTest(test: BenchmarkTest) = dao.updateBenchmarkTest(test)
    suspend fun deleteBenchmarkTest(test: BenchmarkTest) = dao.deleteBenchmarkTest(test)

    suspend fun sendPromptToNox(
        persona: AiPersona,
        userPrompt: String,
        history: List<ChatMessage> = emptyList()
    ): Result<String> = withContext(Dispatchers.Default) {
        val activeDatasets = dao.getActiveTrainingDataList(persona.id)

        val systemPrompt = buildString {
            appendLine("IDENTIDAD Y PERSONALIDAD:")
            appendLine(persona.systemInstruction)
            appendLine()
            appendLine("BASE DE CONOCIMIENTO LOCAL:")
            if (activeDatasets.isEmpty()) {
                appendLine("No hay documentos adicionales.")
            } else {
                activeDatasets.forEachIndexed { index, data ->
                    appendLine("=== FUENTE ${index + 1}: ${data.title} ===")
                    appendLine(data.content)
                    appendLine()
                }
            }
            appendLine("REGLAS DE RESPUESTA:")
            appendLine("- Responde en el idioma del usuario.")
            appendLine("- Mantén la personalidad definida arriba.")
            appendLine("- Usa la base de conocimiento solo cuando sea relevante.")
            appendLine("- No inventes datos presentes en documentos.")
            appendLine("- Sé natural y conversacional.")
        }

        val conversation = buildString {
            appendLine(systemPrompt)
            appendLine()
            history.takeLast(8).forEach { msg ->
                appendLine(if (msg.sender == "USER") "Usuario:" else "Nox:")
                appendLine(msg.text)
                appendLine()
            }
            appendLine("Usuario:")
            appendLine(userPrompt)
            appendLine("Nox:")
        }

        noxEngine.generate(
            prompt = conversation,
            temperature = persona.temperature,
            topK = 40
        )
    }

    /**
     * Dataset synthesis is also local. It is intentionally lightweight and
     * uses the same on-device model rather than a remote provider.
     */
    suspend fun synthesizeTrainingDataLocally(
        rawText: String,
        targetType: String
    ): Result<String> = withContext(Dispatchers.Default) {
        val instruction = when (targetType) {
            "QA_PAIRS" ->
                "Extrae entre 4 y 6 pares de Pregunta (P:) y Respuesta (R:) del siguiente texto."
            "RULES_LIST" ->
                "Extrae las reglas o directrices principales del siguiente texto en viñetas."
            else ->
                "Resume y estructura el siguiente texto como un documento de conocimiento para una IA."
        }

        noxEngine.generate(
            prompt = "$instruction\n\nTexto:\n$rawText\n\nResultado:",
            temperature = 0.3f,
            topK = 30
        )
    }
}
