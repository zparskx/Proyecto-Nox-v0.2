package com.nox.ai.data.local

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import java.io.File

/**
 * On-device LLM engine.
 *
 * The model is deliberately NOT bundled into the APK because Google notes
 * that these model files are too large for normal APK packaging.
 *
 * Expected file:
 *   <app internal files>/models/nox-model.task
 *
 * Use ModelManager.copyModelFromUri() to install a .task model.
 */
class LocalNoxEngine(private val context: Context) {

    companion object {
        const val MODEL_DIRECTORY = "models"
        const val MODEL_FILE = "nox-model.task"
        private const val MAX_TOKENS = 768
    }

    private var inference: LlmInference? = null

    private fun modelFile(): File =
        File(File(context.filesDir, MODEL_DIRECTORY), MODEL_FILE)

    private fun ensureEngine(): LlmInference {
        inference?.let { return it }

        val file = modelFile()
        check(file.exists()) {
            "MODELO_NO_INSTALADO: coloca un modelo MediaPipe .task en ${file.absolutePath}"
        }

        val options = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(file.absolutePath)
            .setMaxTokens(MAX_TOKENS)
            .setMaxTopK(64)
            .build()

        return LlmInference.createFromOptions(context, options).also {
            inference = it
        }
    }

    fun generate(
        prompt: String,
        temperature: Float,
        topK: Int
    ): Result<String> = runCatching {
        val engine = ensureEngine()
        engine.generateResponse(prompt).trim().ifBlank {
            error("El modelo local devolvió una respuesta vacía.")
        }
    }

    fun close() {
        inference?.close()
        inference = null
    }
}
