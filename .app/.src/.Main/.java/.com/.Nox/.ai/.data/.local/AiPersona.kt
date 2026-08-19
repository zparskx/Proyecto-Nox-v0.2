package com.nox.ai.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_personas")
data class AiPersona(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val category: String, // e.g., "Educación", "Programación", "Negocios", "Creatividad", "Personal"
    val systemInstruction: String,
    val baseModel: String = "gemma-3-1b-it-local", // On-device model identifier
    val temperature: Float = 0.7f,
    val topP: Float = 0.95f,
    val iconName: String = "psychology",
    val colorHex: String = "#4285F4",
    val isTrained: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
