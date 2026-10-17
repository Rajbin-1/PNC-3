package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String = "primary",
    val senderId: String, // "me" or "partner"
    val recipientId: String,
    val messageType: String = "TEXT", // "TEXT", "IMAGE", "DOODLE"
    val text: String,
    val mediaUri: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "QUEUED", // "QUEUED", "SYNCING", "SENT", "DELIVERED", "READ", "FAILED"
    val replyToId: String? = null,
    val replyText: String? = null,
    val reaction: String? = null
)

@Entity(tableName = "pair_session")
data class PairSessionEntity(
    @PrimaryKey val id: Int = 1,
    val userId: String = "usr_01H9A",
    val partnerId: String = "usr_01H9B",
    val userName: String = "You",
    val partnerName: String = "Alex",
    val partnerNickname: String = "Alex",
    val catName: String = "Mochi",
    val pairCode: String = "PNC9X4",
    val status: String = "PAIRED", // "UNPAIRED", "PAIRING", "PAIRED"
    val pairedTimestamp: Long = System.currentTimeMillis() - (184L * 86400000L),
    val lastSeenEpoch: Long = System.currentTimeMillis() - (2L * 60000L),
    val sharedSecretMask: String = "••••••••c4e2"
)

@Entity(tableName = "cat_companion")
data class CatEntity(
    @PrimaryKey val id: String = "shared_cat_1",
    val pairId: String = "primary_pair",
    val name: String = "Mochi",
    val appearance: String = "TUXEDO", // "TUXEDO", "GINGER", "ONYX", "SILVER"
    val activeAccessory: String = "BELL_COLLAR", // "NONE", "BELL_COLLAR", "COZY_SCARF", "RIBBON", "STAR_CROWN", "BERET"
    val mood: String = "CONTENT", // "CONTENT", "HAPPY", "SLEEPY", "PLAYFUL"
    val lastInteractionEpoch: Long = System.currentTimeMillis()
)

@Entity(tableName = "shared_activity_ledger")
data class SharedActivityEntity(
    @PrimaryKey val id: String,
    val eventType: String, // "MESSAGING", "CAT_CARE", "MEMORY", "DOODLE"
    val actorId: String,
    val actorName: String,
    val dateString: String, // "YYYY-MM-DD"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "theme_config")
data class ThemeConfigEntity(
    @PrimaryKey val id: Int = 1,
    val themeMode: String = "SYSTEM", // "SYSTEM", "DARK", "LIGHT"
    val darkBackgroundHex: String = "#0D1117",
    val lightBackgroundHex: String = "#F8F9FA",
    val darkSurfaceHex: String = "#161B22",
    val lightSurfaceHex: String = "#FFFFFF",
    val accentHex: String = "#E05368",
    val outgoingBubbleHex: String = "#1F3A60",
    val incomingBubbleHex: String = "#21262D",
    val bubbleRadiusDp: Int = 18,
    val bubbleStylePreset: Int = 0, // 0 to 14 for 15 distinct bubble styles
    val chatBackgroundType: String = "DEFAULT", // "DEFAULT", "SOLID", "PATTERN", "CUSTOM_URI"
    val chatBackgroundUri: String? = null,
    val chatBackgroundOverlayAlpha: Float = 0.25f,
    val hapticsEnabled: Boolean = true
)

@Entity(tableName = "milestones")
data class MilestoneEntity(
    @PrimaryKey val id: String,
    val title: String,
    val dateEpochMillis: Long,
    val category: String = "ANNIVERSARY", // "ANNIVERSARY", "FIRST_DATE", "TRIP", "CUSTOM"
    val notes: String = ""
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey val id: String,
    val caption: String,
    val dateEpochMillis: Long = System.currentTimeMillis(),
    val mediaUri: String,
    val location: String = ""
)

@Entity(tableName = "question_cards")
data class QuestionCardEntity(
    @PrimaryKey val id: String,
    val category: String,
    val questionText: String,
    val myAnswer: String? = null,
    val partnerAnswer: String? = null,
    val isCompleted: Boolean = false
)
