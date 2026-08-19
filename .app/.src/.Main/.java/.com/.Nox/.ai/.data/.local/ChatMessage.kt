package com.nox.ai.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_messages",
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
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personaId: Long,
    val sender: String, // "USER" or "AI"
    val text: String,
    val isGroundedWithData: Boolean = false,
    val activeDatasetsCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isError: Boolean = false
)
