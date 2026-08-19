package com.nox.ai.data.local

import android.content.Context
import android.net.Uri
import java.io.File

/**
 * Installs a downloaded/selected MediaPipe .task model into app-private storage.
 */
object ModelManager {
    fun modelFile(context: Context): File =
        File(File(context.filesDir, LocalNoxEngine.MODEL_DIRECTORY), LocalNoxEngine.MODEL_FILE)

    fun hasModel(context: Context): Boolean = modelFile(context).exists()

    fun copyModelFromUri(context: Context, uri: Uri): File {
        val target = modelFile(context)
        target.parentFile?.mkdirs()

        context.contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "No se pudo abrir el modelo seleccionado." }
            target.outputStream().use { output -> input.copyTo(output) }
        }
        return target
    }

    fun deleteModel(context: Context) {
        modelFile(context).delete()
    }
}
