package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PncScreen
import com.example.ui.theme.LocalPncTheme

data class NavItem(
    val screen: PncScreen,
    val label: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector,
    val tag: String
)

@Composable
fun PncBottomNav(
    currentScreen: PncScreen,
    onSelectScreen: (PncScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalPncTheme.current

    val items = listOf(
        NavItem(PncScreen.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home, "tab_home"),
        NavItem(PncScreen.CHAT, "Chat", Icons.Filled.ChatBubble, Icons.Outlined.ChatBubbleOutline, "tab_chat"),
        NavItem(PncScreen.CALL, "Call", Icons.Filled.Call, Icons.Outlined.Call, "tab_call"),
        NavItem(PncScreen.PLAY, "Play", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder, "tab_play"),
        NavItem(PncScreen.MEMORIES, "Memories", Icons.Filled.PhotoLibrary, Icons.Outlined.PhotoLibrary, "tab_memories")
    )

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(60.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    val interactionSource = remember { MutableInteractionSource() }

                    Column(
                        modifier = Modifier
                            .testTag(item.tag)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { onSelectScreen(item.screen) }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val tint = if (isSelected) theme.accentColor else MaterialTheme.colorScheme.onSurfaceVariant
                        Icon(
                            imageVector = if (isSelected) item.activeIcon else item.inactiveIcon,
                            contentDescription = item.label,
                            tint = tint,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = item.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = tint,
                            letterSpacing = 0.2.sp
                        )
                    }
                }
            }
        }
    }
}
