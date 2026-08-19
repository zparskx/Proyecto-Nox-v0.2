package com.nox.ai.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AiStudioDao {
    // Personas
    @Query("SELECT * FROM ai_personas ORDER BY updatedAt DESC")
    fun getAllPersonas(): Flow<List<AiPersona>>

    @Query("SELECT * FROM ai_personas WHERE id = :id")
    suspend fun getPersonaById(id: Long): AiPersona?

    @Query("SELECT * FROM ai_personas WHERE id = :id")
    fun getPersonaFlowById(id: Long): Flow<AiPersona?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersona(persona: AiPersona): Long

    @Update
    suspend fun updatePersona(persona: AiPersona)

    @Delete
    suspend fun deletePersona(persona: AiPersona)

    // Training Data
    @Query("SELECT * FROM training_data WHERE personaId = :personaId ORDER BY createdAt DESC")
    fun getTrainingDataForPersona(personaId: Long): Flow<List<TrainingData>>

    @Query("SELECT * FROM training_data WHERE personaId = :personaId AND isActive = 1")
    suspend fun getActiveTrainingDataList(personaId: Long): List<TrainingData>

    @Query("SELECT * FROM training_data ORDER BY createdAt DESC")
    fun getAllTrainingData(): Flow<List<TrainingData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrainingData(trainingData: TrainingData): Long

    @Update
    suspend fun updateTrainingData(trainingData: TrainingData)

    @Delete
    suspend fun deleteTrainingData(trainingData: TrainingData)

    // Chat Messages
    @Query("SELECT * FROM chat_messages WHERE personaId = :personaId ORDER BY timestamp ASC")
    fun getChatMessagesForPersona(personaId: Long): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage): Long

    @Query("DELETE FROM chat_messages WHERE personaId = :personaId")
    suspend fun clearChatMessagesForPersona(personaId: Long)

    // Benchmark Tests
    @Query("SELECT * FROM benchmark_tests WHERE personaId = :personaId ORDER BY id DESC")
    fun getBenchmarkTestsForPersona(personaId: Long): Flow<List<BenchmarkTest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBenchmarkTest(test: BenchmarkTest): Long

    @Update
    suspend fun updateBenchmarkTest(test: BenchmarkTest)

    @Delete
    suspend fun deleteBenchmarkTest(test: BenchmarkTest)
}
