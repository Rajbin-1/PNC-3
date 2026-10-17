package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.db.ThemeConfigEntity
import com.example.ui.PncViewModel
import com.example.ui.screens.getBubbleShape
import com.example.ui.theme.LocalPncTheme

val bubblePresetNames = listOf(
    "Clean Rounded", "Soft Pill", "Compact", "Asymmetric",
    "Outlined", "Translucent", "Minimal", "Continuous",
    "Geometric", "Bubble Pop", "Pill Grad", "Neo Brutal",
    "Modern Glass", "Kawaii Soft", "Executive"
)

@Composable
fun SettingsDialog(
    viewModel: PncViewModel,
    onDismiss: () -> Unit
) {
    val theme = LocalPncTheme.current
    val themeConfig by viewModel.themeConfig.collectAsState()
    val currentConfig = themeConfig ?: ThemeConfigEntity()

    var activeTab by remember { mutableStateOf(0) } // 0: Bubble Styles, 1: Chat Wallpaper, 2: Sync & Info

    // Photo picker launcher for chat background wallpaper
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val updated = currentConfig.copy(
                chatBackgroundType = "CUSTOM_URI",
                chatBackgroundUri = uri.toString()
            )
            viewModel.updateTheme(updated)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 36.dp),
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "THEME STUDIO & CUSTOMIZATION",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.accentColor,
                        letterSpacing = 1.sp
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TabChip(
                        title = "Bubble Styles",
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 }
                    )
                    TabChip(
                        title = "Chat Wallpaper",
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 }
                    )
                    TabChip(
                        title = "Sync & Info",
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 }
                    )
                }

                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // Tab Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    when (activeTab) {
                        0 -> {
                            Text(
                                text = "SELECT CHAT BUBBLE STYLE (${bubblePresetNames.size} Presets)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.accentColor,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Choose from 15 distinct, polished messaging bubble styles. Changes apply instantly to all chat messages.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(340.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                userScrollEnabled = true
                            ) {
                                itemsIndexed(bubblePresetNames) { index, name ->
                                    val isSelected = currentConfig.bubbleStylePreset == index
                                    BubblePresetPreviewCard(
                                        name = name,
                                        presetIndex = index,
                                        isSelected = isSelected,
                                        accentColor = theme.accentColor,
                                        onClick = {
                                            viewModel.updateTheme(currentConfig.copy(bubbleStylePreset = index))
                                        }
                                    )
                                }
                            }
                        }

                        1 -> {
                            Text(
                                text = "CHAT BACKGROUND WALLPAPER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.accentColor,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Personalize your conversation background with a photo from your device gallery.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = theme.accentColor,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.padding(4.dp))
                                Text("Choose Photo from Gallery", fontWeight = FontWeight.SemiBold)
                            }

                            if (currentConfig.chatBackgroundType == "CUSTOM_URI") {
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = {
                                        viewModel.updateTheme(currentConfig.copy(chatBackgroundType = "DEFAULT", chatBackgroundUri = null))
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text("Reset to Default Solid Background")
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Wallpaper Dimming Overlay: ${(currentConfig.chatBackgroundOverlayAlpha * 100).toInt()}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Slider(
                                    value = currentConfig.chatBackgroundOverlayAlpha,
                                    onValueChange = { alpha ->
                                        viewModel.updateTheme(currentConfig.copy(chatBackgroundOverlayAlpha = alpha))
                                    },
                                    valueRange = 0.05f..0.7f
                                )
                            }
                        }

                        2 -> {
                            Text(
                                text = "ARCHITECTURE & SYNC STATUS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.accentColor,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "PNC<3 runs locally with Room SQLite storage and zero-trust end-to-end encryption between paired devices.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = { viewModel.resetTheme() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Reset All Theme Defaults")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabChip(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val theme = LocalPncTheme.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) theme.accentColor.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .border(
                width = if (selected) 1.5.dp else 0.5.dp,
                color = if (selected) theme.accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) theme.accentColor else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun BubblePresetPreviewCard(
    name: String,
    presetIndex: Int,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val sampleShape = getBubbleShape(presetIndex, isMe = true, defaultRadius = 14.dp)

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) accentColor.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
            .border(
                width = if (isSelected) 2.dp else 0.5.dp,
                color = if (isSelected) accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .width(72.dp)
                .height(36.dp)
                .clip(sampleShape)
                .background(accentColor)
                .padding(horizontal = 6.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.8f))
            )
        }

        Text(
            text = name,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}
