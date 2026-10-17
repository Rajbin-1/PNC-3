package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.db.CatEntity
import com.example.data.db.MemoryEntity
import com.example.data.db.MessageEntity
import com.example.data.db.MilestoneEntity
import com.example.data.db.PairSessionEntity
import com.example.data.db.QuestionCardEntity
import com.example.data.db.SharedActivityEntity
import com.example.data.db.ThemeConfigEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PncRepository(private val db: AppDatabase) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val messages: Flow<List<MessageEntity>> = db.messageDao().getAllMessages()
    val session: Flow<PairSessionEntity?> = db.pairSessionDao().getSession()
    val themeConfig: Flow<ThemeConfigEntity?> = db.themeConfigDao().getThemeConfig()
    val milestones: Flow<List<MilestoneEntity>> = db.milestoneDao().getAllMilestones()
    val memories: Flow<List<MemoryEntity>> = db.memoryDao().getAllMemories()
    val questions: Flow<List<QuestionCardEntity>> = db.questionCardDao().getAllQuestions()

    val cat: Flow<CatEntity?> = db.catDao().getCat()
    val sharedActivities: Flow<List<SharedActivityEntity>> = db.sharedActivityDao().getAllActivities()

    suspend fun sendMessage(
        text: String,
        replyTo: MessageEntity? = null,
        isOnline: Boolean = true
    ): String {
        val id = "msg_" + UUID.randomUUID().toString().take(8)
        val initialStatus = if (isOnline) "QUEUED" else "QUEUED"
        val message = MessageEntity(
            id = id,
            senderId = "me",
            recipientId = "partner",
            text = text,
            messageType = "TEXT",
            timestamp = System.currentTimeMillis(),
            status = initialStatus,
            replyToId = replyTo?.id,
            replyText = replyTo?.text?.take(40)
        )
        db.messageDao().insertMessage(message)

        val currentSession = session.firstOrNull()
        recordSharedActivity(
            eventType = "MESSAGING",
            actorId = currentSession?.userId ?: "usr_01H9A",
            actorName = currentSession?.userName ?: "You"
        )

        if (isOnline) {
            scope.launch {
                delay(300)
                db.messageDao().updateStatus(id, "SENT")
                delay(600)
                db.messageDao().updateStatus(id, "DELIVERED")
                delay(1200)
                db.messageDao().updateStatus(id, "READ")
            }
        }
        return id
    }

    suspend fun sendDoodleMessage(doodleUri: String, isOnline: Boolean = true): String {
        val id = "doodle_" + UUID.randomUUID().toString().take(8)
        val message = MessageEntity(
            id = id,
            senderId = "me",
            recipientId = "partner",
            text = "Sent a drawing 🎨",
            mediaUri = doodleUri,
            messageType = "DOODLE",
            timestamp = System.currentTimeMillis(),
            status = if (isOnline) "DELIVERED" else "QUEUED"
        )
        db.messageDao().insertMessage(message)

        val currentSession = session.firstOrNull()
        recordSharedActivity(
            eventType = "DOODLE",
            actorId = currentSession?.userId ?: "usr_01H9A",
            actorName = currentSession?.userName ?: "You"
        )
        return id
    }

    suspend fun addReaction(messageId: String, reaction: String?) {
        db.messageDao().updateReaction(messageId, reaction)
    }

    suspend fun deleteMessage(messageId: String) {
        db.messageDao().deleteMessage(messageId)
    }

    suspend fun clearChat() {
        db.messageDao().clearAll()
    }

    // --- Profile Customization ---
    suspend fun updateCustomNames(userName: String, partnerNickname: String, catName: String) {
        val currentSession = session.firstOrNull()
        if (currentSession != null) {
            val updated = currentSession.copy(
                userName = userName.trim().take(24).ifEmpty { "You" },
                partnerNickname = partnerNickname.trim().take(24).ifEmpty { "Alex" },
                catName = catName.trim().take(24).ifEmpty { "Mochi" }
            )
            db.pairSessionDao().insertOrUpdate(updated)
        }
        db.catDao().updateCatName(catName.trim().take(24).ifEmpty { "Mochi" })
    }

    // --- Simplified Cat Tap Interaction ---
    suspend fun interactWithCat() {
        val currentCat = cat.firstOrNull() ?: return
        val currentSession = session.firstOrNull()
        val actor = currentSession?.userName ?: "You"
        val now = System.currentTimeMillis()

        val moods = listOf("HAPPY", "PURRING", "CONTENT", "PLAYFUL")
        val nextMood = moods.random()

        db.catDao().updateMoodAndInteraction(nextMood, now)
        recordSharedActivity("CAT_CARE", "user_me", actor)
    }

    suspend fun updateCatAppearance(appearance: String) {
        db.catDao().updateAppearance(appearance)
    }

    suspend fun updateCatAccessory(accessory: String) {
        db.catDao().updateAccessory(accessory)
    }

    // --- Streak & Activity Ledger ---
    suspend fun recordSharedActivity(eventType: String, actorId: String, actorName: String) {
        val now = System.currentTimeMillis()
        val todayStr = dateFormat.format(Date(now))
        val entry = SharedActivityEntity(
            id = UUID.randomUUID().toString(),
            eventType = eventType,
            actorId = actorId,
            actorName = actorName,
            dateString = todayStr,
            timestamp = now
        )
        db.sharedActivityDao().insertActivity(entry)
    }

    // --- Theme & Customization ---
    suspend fun updateTheme(config: ThemeConfigEntity) {
        db.themeConfigDao().insertOrUpdate(config)
    }

    suspend fun resetThemeDefaults() {
        db.themeConfigDao().insertOrUpdate(
            ThemeConfigEntity(
                id = 1,
                themeMode = "SYSTEM",
                darkBackgroundHex = "#0D1117",
                lightBackgroundHex = "#F8F9FA",
                darkSurfaceHex = "#161B22",
                lightSurfaceHex = "#FFFFFF",
                accentHex = "#E05368",
                outgoingBubbleHex = "#1F3A60",
                incomingBubbleHex = "#21262D",
                bubbleRadiusDp = 18,
                bubbleStylePreset = 0,
                chatBackgroundType = "DEFAULT",
                chatBackgroundUri = null,
                chatBackgroundOverlayAlpha = 0.25f,
                hapticsEnabled = true
            )
        )
    }

    suspend fun updatePairSession(session: PairSessionEntity) {
        db.pairSessionDao().insertOrUpdate(session)
    }

    suspend fun unpair() {
        val currentSession = session.firstOrNull()
        db.pairSessionDao().insertOrUpdate(
            PairSessionEntity(
                id = 1,
                userId = currentSession?.userId ?: "usr_01H9A",
                partnerId = "",
                userName = currentSession?.userName ?: "You",
                partnerName = "Unpaired",
                partnerNickname = "Unpaired",
                catName = currentSession?.catName ?: "Mochi",
                pairCode = "PNC" + (1000..9999).random(),
                status = "UNPAIRED",
                pairedTimestamp = 0L,
                lastSeenEpoch = 0L,
                sharedSecretMask = "None"
            )
        )
    }

    suspend fun addMilestone(title: String, category: String, notes: String, dateEpochMillis: Long) {
        val milestone = MilestoneEntity(
            id = "m_" + UUID.randomUUID().toString().take(6),
            title = title,
            category = category,
            notes = notes,
            dateEpochMillis = dateEpochMillis
        )
        db.milestoneDao().insertMilestone(milestone)
    }

    suspend fun deleteMilestone(id: String) {
        db.milestoneDao().deleteMilestone(id)
    }

    suspend fun addMemory(caption: String, uri: String, location: String = "") {
        val memory = MemoryEntity(
            id = "mem_" + UUID.randomUUID().toString().take(6),
            caption = caption,
            mediaUri = uri,
            location = location,
            dateEpochMillis = System.currentTimeMillis()
        )
        db.memoryDao().insertMemory(memory)

        val currentSession = session.firstOrNull()
        recordSharedActivity(
            eventType = "MEMORY",
            actorId = currentSession?.userId ?: "usr_01H9A",
            actorName = currentSession?.userName ?: "You"
        )
    }

    suspend fun deleteMemory(id: String) {
        db.memoryDao().deleteMemory(id)
    }

    suspend fun answerQuestion(question: QuestionCardEntity, myAnswer: String) {
        val updated = question.copy(
            myAnswer = myAnswer,
            isCompleted = question.partnerAnswer != null
        )
        db.questionCardDao().updateQuestion(updated)
    }

    suspend fun receiveSimulatedPartnerMessage(text: String) {
        val id = "msg_sim_" + UUID.randomUUID().toString().take(8)
        val msg = MessageEntity(
            id = id,
            senderId = "partner",
            recipientId = "me",
            text = text,
            timestamp = System.currentTimeMillis(),
            status = "READ"
        )
        db.messageDao().insertMessage(msg)

        val currentSession = session.firstOrNull()
        recordSharedActivity(
            eventType = "MESSAGING",
            actorId = currentSession?.partnerId ?: "usr_01H9B",
            actorName = currentSession?.partnerNickname ?: "Alex"
        )
    }
}
