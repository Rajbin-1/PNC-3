package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("UPDATE messages SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("UPDATE messages SET reaction = :reaction WHERE id = :id")
    suspend fun updateReaction(id: String, reaction: String?)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessage(id: String)

    @Query("DELETE FROM messages")
    suspend fun clearAll()
}

@Dao
interface PairSessionDao {
    @Query("SELECT * FROM pair_session WHERE id = 1 LIMIT 1")
    fun getSession(): Flow<PairSessionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(session: PairSessionEntity)

    @Query("UPDATE pair_session SET userName = :userName, partnerNickname = :partnerNickname, catName = :catName WHERE id = 1")
    suspend fun updateCustomNames(userName: String, partnerNickname: String, catName: String)
}

@Dao
interface CatDao {
    @Query("SELECT * FROM cat_companion WHERE id = 'shared_cat_1' LIMIT 1")
    fun getCat(): Flow<CatEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(cat: CatEntity)

    @Query("UPDATE cat_companion SET name = :name WHERE id = 'shared_cat_1'")
    suspend fun updateCatName(name: String)

    @Query("UPDATE cat_companion SET appearance = :appearance WHERE id = 'shared_cat_1'")
    suspend fun updateAppearance(appearance: String)

    @Query("UPDATE cat_companion SET activeAccessory = :accessory WHERE id = 'shared_cat_1'")
    suspend fun updateAccessory(accessory: String)

    @Query("UPDATE cat_companion SET mood = :mood, lastInteractionEpoch = :epoch WHERE id = 'shared_cat_1'")
    suspend fun updateMoodAndInteraction(mood: String, epoch: Long)
}

@Dao
interface SharedActivityDao {
    @Query("SELECT * FROM shared_activity_ledger ORDER BY timestamp DESC")
    fun getAllActivities(): Flow<List<SharedActivityEntity>>

    @Query("SELECT * FROM shared_activity_ledger WHERE dateString = :dateString")
    fun getActivitiesForDate(dateString: String): Flow<List<SharedActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: SharedActivityEntity)
}

@Dao
interface ThemeConfigDao {
    @Query("SELECT * FROM theme_config WHERE id = 1 LIMIT 1")
    fun getThemeConfig(): Flow<ThemeConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(config: ThemeConfigEntity)
}

@Dao
interface MilestoneDao {
    @Query("SELECT * FROM milestones ORDER BY dateEpochMillis ASC")
    fun getAllMilestones(): Flow<List<MilestoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: MilestoneEntity)

    @Query("DELETE FROM milestones WHERE id = :id")
    suspend fun deleteMilestone(id: String)
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY dateEpochMillis DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity)

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemory(id: String)
}

@Dao
interface QuestionCardDao {
    @Query("SELECT * FROM question_cards")
    fun getAllQuestions(): Flow<List<QuestionCardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionCardEntity>)

    @Update
    suspend fun updateQuestion(question: QuestionCardEntity)
}
