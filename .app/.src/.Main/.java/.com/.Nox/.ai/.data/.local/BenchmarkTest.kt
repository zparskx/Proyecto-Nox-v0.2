package com.nox.ai.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "benchmark_tests",
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
data class BenchmarkTest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personaId: Long,
    val prompt: String,
    val expectedKeywords: String = "",
    val actualResponse: String? = null,
    val ratingScore: Int? = null, // 1 to 5 stars
    val testedAt: Long? = null
)
