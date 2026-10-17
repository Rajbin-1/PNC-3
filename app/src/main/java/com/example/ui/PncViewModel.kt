package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.CatEntity
import com.example.data.db.MemoryEntity
import com.example.data.db.MessageEntity
import com.example.data.db.MilestoneEntity
import com.example.data.db.PairSessionEntity
import com.example.data.db.QuestionCardEntity
import com.example.data.db.SharedActivityEntity
import com.example.data.db.ThemeConfigEntity
import com.example.data.repository.PncRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class PncScreen {
    HOME, CHAT, CALL, PLAY, MEMORIES
}

enum class CallState {
    IDLE, OUTGOING, RINGING, CONNECTED, ENDED
}

data class DoodleStroke(
    val points: List<Pair<Float, Float>>,
    val colorHex: String,
    val strokeWidth: Float
)

data class StreakInfo(
    val currentStreakDays: Int,
    val completedToday: Boolean,
    val nextMilestoneDays: Int,
    val milestoneStage: Int, // 1 to 7 corresponding to yarn ball stages
    val milestoneTitle: String
)

class PncViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = PncRepository(database)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Active Screen
    private val _currentScreen = MutableStateFlow(PncScreen.HOME)
    val currentScreen: StateFlow<PncScreen> = _currentScreen.asStateFlow()

    // Modals
    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _isPairDialogOpen = MutableStateFlow(false)
    val isPairDialogOpen: StateFlow<Boolean> = _isPairDialogOpen.asStateFlow()

    private val _isProfileOpen = MutableStateFlow(false)
    val isProfileOpen: StateFlow<Boolean> = _isProfileOpen.asStateFlow()

    // Interactive action toast / feedback
    private val _catFeedback = MutableStateFlow<String?>(null)
    val catFeedback: StateFlow<String?> = _catFeedback.asStateFlow()

    // Data streams
    val messages: StateFlow<List<MessageEntity>> = repository.messages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val session: StateFlow<PairSessionEntity?> = repository.session
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val themeConfig: StateFlow<ThemeConfigEntity?> = repository.themeConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val milestones: StateFlow<List<MilestoneEntity>> = repository.milestones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memories: StateFlow<List<MemoryEntity>> = repository.memories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val questions: StateFlow<List<QuestionCardEntity>> = repository.questions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cat: StateFlow<CatEntity?> = repository.cat
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val sharedActivities: StateFlow<List<SharedActivityEntity>> = repository.sharedActivities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chat Composer State
    val messageInput = MutableStateFlow("")
    val replyingTo = MutableStateFlow<MessageEntity?>(null)
    val isOnline = MutableStateFlow(true)

    // Call System State Machine
    val callState = MutableStateFlow(CallState.IDLE)
    val isVideoCall = MutableStateFlow(false)
    val isMuted = MutableStateFlow(false)
    val isCameraOff = MutableStateFlow(false)
    val isSpeakerOn = MutableStateFlow(true)
    val isFrontCamera = MutableStateFlow(true)
    val callDurationSeconds = MutableStateFlow(0)
    private var callTimerJob: Job? = null

    // Doodle Canvas State
    val strokes = MutableStateFlow<List<DoodleStroke>>(emptyList())
    val currentBrushColorHex = MutableStateFlow("#E05368")
    val currentBrushWidth = MutableStateFlow(8f)

    init {
        viewModelScope.launch {
            AppDatabase.seedDefaults(database)
        }
    }

    fun navigateTo(screen: PncScreen) {
        _currentScreen.value = screen
    }

    fun openSettings(open: Boolean) {
        _isSettingsOpen.value = open
    }

    fun openPairDialog(open: Boolean) {
        _isPairDialogOpen.value = open
    }

    fun openProfile(open: Boolean) {
        _isProfileOpen.value = open
    }

    fun showFeedback(message: String) {
        _catFeedback.value = message
        viewModelScope.launch {
            delay(2800)
            if (_catFeedback.value == message) {
                _catFeedback.value = null
            }
        }
    }

    fun toggleOnline() {
        isOnline.value = !isOnline.value
    }

    // --- Profile Customization ---
    fun updateProfiles(userName: String, partnerNickname: String, catName: String) {
        viewModelScope.launch {
            repository.updateCustomNames(userName, partnerNickname, catName)
            _isProfileOpen.value = false
            showFeedback("Profiles updated ✨")
        }
    }

    // --- Simplified Cat Companion Interaction ---
    fun interactWithCat() {
        val currentCat = cat.value ?: return
        viewModelScope.launch {
            repository.interactWithCat()
            val reactions = listOf(
                "Purr... ${currentCat.name} leaned into your hand 🐾",
                "Meow! ${currentCat.name} looks so happy ❤️",
                "${currentCat.name} gives a gentle headbump ✨",
                "Prrr... cozy companion mode activated 🧶"
            )
            showFeedback(reactions.random())
        }
    }

    fun updateCatAppearance(appearance: String) {
        viewModelScope.launch {
            repository.updateCatAppearance(appearance)
            showFeedback("Coat updated to $appearance 🐾")
        }
    }

    fun updateCatAccessory(accessory: String) {
        viewModelScope.launch {
            repository.updateCatAccessory(accessory)
            showFeedback("Accessory updated! ✨")
        }
    }

    // --- Streak & Yarn-Ball Stage Calculation ---
    fun calculateStreakInfo(activities: List<SharedActivityEntity>): StreakInfo {
        val now = System.currentTimeMillis()
        val todayStr = dateFormat.format(Date(now))

        val activitiesToday = activities.filter { it.dateString == todayStr }
        val completedToday = activitiesToday.isNotEmpty()

        val datesWithActivity = activities.map { it.dateString }.toSet()
        var streakDays = 0
        var checkEpoch = if (completedToday) now else now - 86400000L

        while (true) {
            val checkDateStr = dateFormat.format(Date(checkEpoch))
            if (datesWithActivity.contains(checkDateStr)) {
                streakDays++
                checkEpoch -= 86400000L
            } else {
                break
            }
        }

        val currentStreak = streakDays.coerceAtLeast(if (completedToday) 1 else 0)

        // Milestones & Yarn Ball Stages (1 to 7)
        val (nextMilestone, stage, title) = when {
            currentStreak < 3 -> Triple(3, 1, "Cozy Ball of Yarn")
            currentStreak < 7 -> Triple(7, 2, "Wound Yarn Ball")
            currentStreak < 14 -> Triple(14, 3, "Textured Yarn Sphere")
            currentStreak < 30 -> Triple(30, 4, "Intertwined Yarn Globe")
            currentStreak < 60 -> Triple(60, 5, "Golden-Threaded Yarn")
            currentStreak < 100 -> Triple(100, 6, "Majestic Crown Yarn")
            else -> Triple(365, 7, "Legendary Master Yarn")
        }

        return StreakInfo(
            currentStreakDays = currentStreak,
            completedToday = completedToday,
            nextMilestoneDays = nextMilestone,
            milestoneStage = stage,
            milestoneTitle = title
        )
    }

    // Chat actions
    fun sendTextMessage() {
        val text = messageInput.value.trim()
        if (text.isEmpty()) return
        val reply = replyingTo.value
        messageInput.value = ""
        replyingTo.value = null

        viewModelScope.launch {
            repository.sendMessage(text, reply, isOnline.value)
        }
    }

    fun setReply(message: MessageEntity?) {
        replyingTo.value = message
    }

    fun addReaction(messageId: String, emoji: String) {
        viewModelScope.launch {
            repository.addReaction(messageId, emoji)
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            repository.deleteMessage(messageId)
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    fun simulatePartnerMessage(text: String) {
        viewModelScope.launch {
            repository.receiveSimulatedPartnerMessage(text)
        }
    }

    // Call actions
    fun startCall(video: Boolean) {
        isVideoCall.value = video
        callState.value = CallState.OUTGOING
        callDurationSeconds.value = 0
        callTimerJob?.cancel()

        viewModelScope.launch {
            delay(1500)
            if (callState.value == CallState.OUTGOING) {
                callState.value = CallState.RINGING
                delay(2000)
                if (callState.value == CallState.RINGING) {
                    callState.value = CallState.CONNECTED
                    startCallTimer()
                }
            }
        }
    }

    private fun startCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (callState.value == CallState.CONNECTED) {
                delay(1000)
                callDurationSeconds.value += 1
            }
        }
    }

    fun endCall() {
        callTimerJob?.cancel()
        callState.value = CallState.ENDED
        viewModelScope.launch {
            delay(1200)
            callState.value = CallState.IDLE
            callDurationSeconds.value = 0
        }
    }

    fun toggleMute() { isMuted.value = !isMuted.value }
    fun toggleCamera() { isCameraOff.value = !isCameraOff.value }
    fun toggleSpeaker() { isSpeakerOn.value = !isSpeakerOn.value }
    fun flipCamera() { isFrontCamera.value = !isFrontCamera.value }

    // Doodle Canvas
    fun addStroke(stroke: DoodleStroke) { strokes.value = strokes.value + stroke }
    fun undoStroke() { if (strokes.value.isNotEmpty()) strokes.value = strokes.value.dropLast(1) }
    fun clearCanvas() { strokes.value = emptyList() }
    fun shareDoodleToChat() {
        if (strokes.value.isEmpty()) return
        viewModelScope.launch {
            repository.sendDoodleMessage("doodle_canvas", isOnline.value)
            _currentScreen.value = PncScreen.CHAT
        }
    }

    // Memories & Milestones
    fun addMemory(caption: String, uri: String, location: String = "") {
        viewModelScope.launch { repository.addMemory(caption, uri, location) }
    }
    fun deleteMemory(id: String) {
        viewModelScope.launch { repository.deleteMemory(id) }
    }
    fun addMilestone(title: String, category: String, notes: String, dateMillis: Long) {
        viewModelScope.launch { repository.addMilestone(title, category, notes, dateMillis) }
    }
    fun deleteMilestone(id: String) {
        viewModelScope.launch { repository.deleteMilestone(id) }
    }

    // Themes & Customization
    fun updateTheme(config: ThemeConfigEntity) {
        viewModelScope.launch { repository.updateTheme(config) }
    }
    fun resetTheme() {
        viewModelScope.launch { repository.resetThemeDefaults() }
    }

    // Pairing
    fun pairWithPartner(code: String) {
        viewModelScope.launch {
            val currentSession = session.value
            val session = PairSessionEntity(
                id = 1,
                userId = currentSession?.userId ?: "usr_01H9A",
                partnerId = "usr_01H9B",
                userName = currentSession?.userName ?: "You",
                partnerName = "Partner",
                partnerNickname = "Partner",
                catName = currentSession?.catName ?: "Mochi",
                pairCode = code.uppercase(),
                status = "PAIRED",
                pairedTimestamp = System.currentTimeMillis(),
                lastSeenEpoch = System.currentTimeMillis(),
                sharedSecretMask = "••••••••e8a1"
            )
            repository.updatePairSession(session)
            _isPairDialogOpen.value = false
        }
    }

    fun unpair() {
        viewModelScope.launch {
            repository.unpair()
            _isPairDialogOpen.value = false
        }
    }
}
