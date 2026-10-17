package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneForwarded
import androidx.compose.material.icons.filled.PhoneMissed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CallState
import com.example.ui.PncViewModel
import com.example.ui.theme.LocalPncTheme
import com.example.ui.theme.PncEmeraldStatus

@Composable
fun CallScreen(
    viewModel: PncViewModel,
    modifier: Modifier = Modifier
) {
    val callState by viewModel.callState.collectAsState()
    val isVideo by viewModel.isVideoCall.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val isCameraOff by viewModel.isCameraOff.collectAsState()
    val isSpeakerOn by viewModel.isSpeakerOn.collectAsState()
    val isFrontCamera by viewModel.isFrontCamera.collectAsState()
    val durationSeconds by viewModel.callDurationSeconds.collectAsState()
    val session by viewModel.session.collectAsState()
    val theme = LocalPncTheme.current

    val partnerName = session?.partnerName?.ifEmpty { "Partner" } ?: "Partner"

    val durationFormatted = remember(durationSeconds) {
        val minutes = durationSeconds / 60
        val seconds = durationSeconds % 60
        String.format("%02d:%02d", minutes, seconds)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (callState) {
            CallState.IDLE -> {
                IdleCallView(
                    partnerName = partnerName,
                    onStartVoiceCall = { viewModel.startCall(video = false) },
                    onStartVideoCall = { viewModel.startCall(video = true) }
                )
            }
            CallState.OUTGOING, CallState.RINGING -> {
                ConnectingCallView(
                    partnerName = partnerName,
                    isRinging = callState == CallState.RINGING,
                    isVideo = isVideo,
                    onEndCall = { viewModel.endCall() }
                )
            }
            CallState.CONNECTED -> {
                ActiveCallView(
                    partnerName = partnerName,
                    duration = durationFormatted,
                    isVideo = isVideo,
                    isMuted = isMuted,
                    isCameraOff = isCameraOff,
                    isSpeakerOn = isSpeakerOn,
                    isFrontCamera = isFrontCamera,
                    onToggleMute = { viewModel.toggleMute() },
                    onToggleCamera = { viewModel.toggleCamera() },
                    onToggleSpeaker = { viewModel.toggleSpeaker() },
                    onFlipCamera = { viewModel.flipCamera() },
                    onEndCall = { viewModel.endCall() }
                )
            }
            CallState.ENDED -> {
                EndedCallView(
                    partnerName = partnerName,
                    duration = durationFormatted
                )
            }
        }
    }
}

@Composable
fun IdleCallView(
    partnerName: String,
    onStartVoiceCall: () -> Unit,
    onStartVideoCall: () -> Unit
) {
    val theme = LocalPncTheme.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            // Partner Avatar placeholder
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(theme.accentColor.copy(alpha = 0.35f), theme.accentColor.copy(alpha = 0.05f))
                        )
                    )
                    .border(1.dp, theme.accentColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = partnerName.take(1).uppercase(),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = partnerName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "P2P WebRTC Direct Channel Ready",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Action Buttons: Voice and Video
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onStartVoiceCall,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .testTag("start_voice_call_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Voice Call",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Voice Call", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            }

            Button(
                onClick = onStartVideoCall,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = theme.accentColor
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .testTag("start_video_call_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Video Call",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Video Call", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }

        // Recent Call Logs
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    0.5.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    RoundedCornerShape(20.dp)
                )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "RECENT SESSIONS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                RecentCallRow(type = "Video Call", duration = "24m 12s", timeAgo = "Yesterday")
                RecentCallRow(type = "Voice Call", duration = "08m 45s", timeAgo = "Oct 14")
                RecentCallRow(type = "Voice Call", duration = "42m 00s", timeAgo = "Oct 11")
            }
        }
    }
}

@Composable
fun RecentCallRow(type: String, duration: String, timeAgo: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                imageVector = if (type.contains("Video")) Icons.Default.Videocam else Icons.Default.Phone,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            Text(type, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground)
        }
        Text("$duration • $timeAgo", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ConnectingCallView(
    partnerName: String,
    isRinging: Boolean,
    isVideo: Boolean,
    onEndCall: () -> Unit
) {
    val theme = LocalPncTheme.current
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(theme.accentColor.copy(alpha = 0.2f))
                    .border(1.5.dp, theme.accentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = partnerName.take(1).uppercase(),
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = partnerName,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isRinging) "Ringing partner's phone..." else "Establishing secure signal...",
                fontSize = 14.sp,
                color = theme.accentColor
            )
        }

        // Cancel / End button
        IconButton(
            onClick = onEndCall,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFFE53935))
        ) {
            Icon(
                imageVector = Icons.Default.CallEnd,
                contentDescription = "Cancel Call",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun ActiveCallView(
    partnerName: String,
    duration: String,
    isVideo: Boolean,
    isMuted: Boolean,
    isCameraOff: Boolean,
    isSpeakerOn: Boolean,
    isFrontCamera: Boolean,
    onToggleMute: () -> Unit,
    onToggleCamera: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onFlipCamera: () -> Unit,
    onEndCall: () -> Unit
) {
    val theme = LocalPncTheme.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top info bar
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text(
                text = partnerName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(PncEmeraldStatus)
                )
                Text(
                    text = duration,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Video preview or audio visualization canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .border(
                    0.5.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isVideo && !isCameraOff) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = null,
                        tint = theme.accentColor,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Encrypted Video Stream",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isFrontCamera) "Front Camera" else "Rear Camera",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(theme.accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = theme.accentColor,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Voice Encrypted P2P",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // In-Call Action Control Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mute Button
            IconButton(
                onClick = onToggleMute,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(if (isMuted) Color(0xFFE53935) else MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Mute",
                    tint = if (isMuted) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }

            // Video Toggle Button
            if (isVideo) {
                IconButton(
                    onClick = onToggleCamera,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (isCameraOff) Color(0xFFE53935) else MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = if (isCameraOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                        contentDescription = "Camera Toggle",
                        tint = if (isCameraOff) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Camera Flip
                IconButton(
                    onClick = onFlipCamera,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Flip Camera",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Speaker Toggle
            IconButton(
                onClick = onToggleSpeaker,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(if (isSpeakerOn) theme.accentColor else MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Speaker",
                    tint = if (isSpeakerOn) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }

            // End Call Button
            IconButton(
                onClick = onEndCall,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE53935))
                    .testTag("end_call_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.CallEnd,
                    contentDescription = "End Call",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
fun EndedCallView(partnerName: String, duration: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CallEnd,
                contentDescription = null,
                tint = Color(0xFFE53935),
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Call Ended",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Duration: $duration with $partnerName",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
