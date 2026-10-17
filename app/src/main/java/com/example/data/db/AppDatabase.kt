package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Database(
    entities = [
        MessageEntity::class,
        PairSessionEntity::class,
        CatEntity::class,
        SharedActivityEntity::class,
        ThemeConfigEntity::class,
        MilestoneEntity::class,
        MemoryEntity::class,
        QuestionCardEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun pairSessionDao(): PairSessionDao
    abstract fun catDao(): CatDao
    abstract fun sharedActivityDao(): SharedActivityDao
    abstract fun themeConfigDao(): ThemeConfigDao
    abstract fun milestoneDao(): MilestoneDao
    abstract fun memoryDao(): MemoryDao
    abstract fun questionCardDao(): QuestionCardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pnc3_shared.db"
                )
                .fallbackToDestructiveMigration(true)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getInstance(context)
                            seedDefaults(database)
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun seedDefaults(database: AppDatabase) {
            val now = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = dateFormat.format(Date(now))
            val yesterdayStr = dateFormat.format(Date(now - 86400000L))
            val twoDaysAgoStr = dateFormat.format(Date(now - 172800000L))
            val threeDaysAgoStr = dateFormat.format(Date(now - 259200000L))

            database.pairSessionDao().insertOrUpdate(
                PairSessionEntity(
                    id = 1,
                    userId = "usr_01H9A",
                    partnerId = "usr_01H9B",
                    userName = "Jordan",
                    partnerName = "Alex",
                    partnerNickname = "Alex",
                    catName = "Mochi",
                    pairCode = "PNC9X4",
                    status = "PAIRED",
                    pairedTimestamp = now - (184L * 86400000L),
                    lastSeenEpoch = now - (2L * 60000L),
                    sharedSecretMask = "••••••••c4e2"
                )
            )

            // Seed Simplified Living Cat Companion
            database.catDao().insertOrUpdate(
                CatEntity(
                    id = "shared_cat_1",
                    pairId = "primary_pair",
                    name = "Mochi",
                    appearance = "TUXEDO",
                    activeAccessory = "BELL_COLLAR",
                    mood = "HAPPY",
                    lastInteractionEpoch = now - (45L * 60000L)
                )
            )

            // Seed Shared Activity Ledger for 4-day continuous streak
            database.sharedActivityDao().insertActivity(
                SharedActivityEntity(
                    id = UUID.randomUUID().toString(),
                    eventType = "CAT_CARE",
                    actorId = "usr_01H9A",
                    actorName = "Jordan",
                    dateString = todayStr,
                    timestamp = now - (45L * 60000L)
                )
            )
            database.sharedActivityDao().insertActivity(
                SharedActivityEntity(
                    id = UUID.randomUUID().toString(),
                    eventType = "MESSAGING",
                    actorId = "usr_01H9B",
                    actorName = "Alex",
                    dateString = yesterdayStr,
                    timestamp = now - 86400000L
                )
            )
            database.sharedActivityDao().insertActivity(
                SharedActivityEntity(
                    id = UUID.randomUUID().toString(),
                    eventType = "DOODLE",
                    actorId = "usr_01H9A",
                    actorName = "Jordan",
                    dateString = twoDaysAgoStr,
                    timestamp = now - 172800000L
                )
            )
            database.sharedActivityDao().insertActivity(
                SharedActivityEntity(
                    id = UUID.randomUUID().toString(),
                    eventType = "MEMORY",
                    actorId = "usr_01H9B",
                    actorName = "Alex",
                    dateString = threeDaysAgoStr,
                    timestamp = now - 259200000L
                )
            )

            database.themeConfigDao().insertOrUpdate(
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

            database.milestoneDao().insertMilestone(
                MilestoneEntity(
                    id = "m1",
                    title = "First Date at Rooftop Cafe",
                    dateEpochMillis = now - (184L * 86400000L),
                    category = "FIRST_DATE",
                    notes = "We talked until the cafe closed."
                )
            )
            database.milestoneDao().insertMilestone(
                MilestoneEntity(
                    id = "m2",
                    title = "Adopted Mochi Together",
                    dateEpochMillis = now - (60L * 86400000L),
                    category = "CUSTOM",
                    notes = "Tiny bundle of energy found a home."
                )
            )

            // Seed initial messages with truthful statuses
            database.messageDao().insertMessage(
                MessageEntity(
                    id = "msg_init_1",
                    senderId = "partner",
                    recipientId = "me",
                    text = "Hey! Made it to the cafe early. Mochi was purring when I left.",
                    timestamp = now - (25L * 60000L),
                    status = "READ",
                    reaction = "❤️"
                )
            )
            database.messageDao().insertMessage(
                MessageEntity(
                    id = "msg_init_2",
                    senderId = "me",
                    recipientId = "partner",
                    text = "Nice! Grab our usual corner table if it's free. Heading over now.",
                    timestamp = now - (22L * 60000L),
                    status = "READ"
                )
            )
            database.messageDao().insertMessage(
                MessageEntity(
                    id = "msg_init_3",
                    senderId = "partner",
                    recipientId = "me",
                    text = "Done! Don't forget your jacket, it's getting chilly.",
                    timestamp = now - (18L * 60000L),
                    status = "READ"
                )
            )
        }
    }
}
