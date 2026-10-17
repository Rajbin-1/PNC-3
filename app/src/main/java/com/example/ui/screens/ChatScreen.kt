package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.db.MessageEntity
import com.example.ui.PncViewModel
import com.example.ui.theme.LocalPncTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    viewModel: PncViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messages.collectAsState()
    val session by viewModel.session.collectAsState()
    val themeConfig by viewModel.themeConfig.collectAsState()
    val inputText by viewModel.messageInput.collectAsState()
    val replyingTo by viewModel.replyingTo.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val theme = LocalPncTheme.current

    val partnerDisplayName = session?.partnerNickname?.ifEmpty { session?.partnerName } ?: "Alex"
    val listState = rememberLazyListState()
    var selectedMessageForReaction by remember { mutableStateOf<MessageEntity?>(null) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Optional Custom Chat Background Wallpaper
        val bgType = themeConfig?.chatBackgroundType ?: "DEFAULT"
        val bgUri = themeConfig?.chatBackgroundUri
        val overlayAlpha = themeConfig?.chatBackgroundOverlayAlpha ?: 0.25f

        if (bgType == "CUSTOM_URI" && !bgUri.isNullOrEmpty()) {
            AsyncImage(
                model = bgUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            // Dimming overlay for text legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = overlayAlpha))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            // Quick simulation toolbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isOnline) "Encrypted Channel • Active" else "Offline Queue Active",
                    fontSize = 11.sp,
                    color = if (isOnline) MaterialTheme.colorScheme.onSurfaceVariant else theme.accentColor
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .clickable {
                            val replies = listOf(
                                "Counting down the hours until I see you! ❤️",
                                "Look what I just saw, reminded me of you.",
                                "Just finished my work session. Coffee later?",
                                "Love you so much PNC<3"
                            )
                            viewModel.simulatePartnerMessage(replies.random())
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "Simulate Partner",
                            tint = theme.accentColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "+ $partnerDisplayName Reply",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Messages timeline
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔒 Messages between exactly 2 devices • Local-first storage",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                items(messages, key = { it.id }) { message ->
                    MessageBubbleItem(
                        message = message,
                        bubbleStylePreset = themeConfig?.bubbleStylePreset ?: 0,
                        onSelectReaction = { selectedMessageForReaction = message },
                        onReply = { viewModel.setReply(message) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // Reply quote indicator
            AnimatedVisibility(visible = replyingTo != null) {
                replyingTo?.let { replyTarget ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Reply,
                                contentDescription = null,
                                tint = theme.accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Replying to: \"${replyTarget.text.take(30)}...\"",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                        IconButton(
                            onClick = { viewModel.setReply(null) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel reply",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Message input composer
            Surface(
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { viewModel.messageInput.value = it },
                        placeholder = { Text("Message $partnerDisplayName...", fontSize = 14.sp) },
                        singleLine = false,
                        maxLines = 4,
                        shape = RoundedCornerShape(22.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = theme.accentColor,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { viewModel.sendTextMessage() }),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field")
                    )

                    IconButton(
                        onClick = { viewModel.sendTextMessage() },
                        enabled = inputText.isNotBlank(),
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                if (inputText.isNotBlank()) theme.accentColor
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .testTag("chat_send_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank()) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubbleItem(
    message: MessageEntity,
    bubbleStylePreset: Int,
    onSelectReaction: () -> Unit,
    onReply: () -> Unit
) {
    val theme = LocalPncTheme.current
    val isMe = message.senderId == "me"

    val timeStr = remember(message.timestamp) {
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp))
    }

    val bubbleShape = remember(bubbleStylePreset, theme.bubbleRadius, isMe) {
        getBubbleShape(bubbleStylePreset, isMe, theme.bubbleRadius)
    }

    val bubbleColor = if (isMe) theme.outgoingBubbleColor else theme.incomingBubbleColor

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        if (message.replyText != null) {
            Box(
                modifier = Modifier
                    .padding(bottom = 3.dp, start = if (isMe) 0.dp else 8.dp, end = if (isMe) 8.dp else 0.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "↪ ${message.replyText}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Box(
            modifier = Modifier
                .widthIn(min = 60.dp, max = 280.dp)
                .clip(bubbleShape)
                .background(bubbleColor)
                .clickable { onSelectReaction() }
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column {
                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    color = Color.White,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = timeStr,
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.65f)
                    )

                    if (isMe) {
                        when (message.status) {
                            "QUEUED" -> {
                                Icon(
                                    imageVector = Icons.Default.HourglassBottom,
                                    contentDescription = "Queued",
                                    tint = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                            "SENT" -> {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "Sent",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            "DELIVERED" -> {
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = "Delivered",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            "READ" -> {
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = "Read",
                                    tint = theme.accentColor,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (message.reaction != null) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        0.5.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = message.reaction, fontSize = 12.sp)
            }
        }
    }
}

fun getBubbleShape(preset: Int, isMe: Boolean, defaultRadius: androidx.compose.ui.unit.Dp): RoundedCornerShape {
    return when (preset) {
        0 -> if (isMe) RoundedCornerShape(defaultRadius, defaultRadius, defaultRadius, 4.dp) else RoundedCornerShape(defaultRadius, defaultRadius, 4.dp, defaultRadius) // Clean Rounded
        1 -> RoundedCornerShape(24.dp) // Soft Pill
        2 -> RoundedCornerShape(6.dp) // Compact Rectangle
        3 -> if (isMe) RoundedCornerShape(defaultRadius, 4.dp, defaultRadius, defaultRadius) else RoundedCornerShape(4.dp, defaultRadius, defaultRadius, defaultRadius) // Gentle Asymmetric
        4 -> if (isMe) RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp) else RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp) // Subtle Outlined
        5 -> RoundedCornerShape(20.dp) // Soft Translucent
        6 -> RoundedCornerShape(2.dp) // Minimal Flat
        7 -> RoundedCornerShape(22.dp, 22.dp, 22.dp, 8.dp) // Smooth Continuous
        8 -> RoundedCornerShape(4.dp) // Compact Geometric
        9 -> RoundedCornerShape(26.dp, 26.dp, 12.dp, 26.dp) // Bubble Pop
        10 -> RoundedCornerShape(24.dp) // Pill Gradient
        11 -> RoundedCornerShape(4.dp) // Neo Brutal
        12 -> RoundedCornerShape(18.dp) // Modern Glass
        13 -> RoundedCornerShape(28.dp, 28.dp, 28.dp, 12.dp) // Kawaii Soft
        else -> RoundedCornerShape(14.dp, 14.dp, 14.dp, 4.dp) // Executive Premium
    }
}
