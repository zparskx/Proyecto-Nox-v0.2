package com.nox.ai.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "training_data",
    foreignKeys = [
        ForeignKey(
            entity = AiPersona::class,
            parentColumns = ["id"],
            childColumns = ["personaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["personaId"])]
)
data class TrainingData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personaId: Long,
    val title: String,
    val contentType: String, // "TEXT_DOC", "QA_PAIRS", "RULES_LIST", "CODE_SNIPPETS"
    val content: String,
    val isActive: Boolean = true,
    val tokenEstimate: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
