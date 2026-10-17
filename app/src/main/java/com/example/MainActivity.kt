package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.PncScreen
import com.example.ui.PncViewModel
import com.example.ui.components.PairingDialog
import com.example.ui.components.PncBottomNav
import com.example.ui.components.PncTopBar
import com.example.ui.components.ProfileCustomizationDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.screens.CallScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MemoriesScreen
import com.example.ui.screens.PlayScreen
import com.example.ui.theme.PncTheme

class MainActivity : ComponentActivity() {
    private val viewModel: PncViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeConfig by viewModel.themeConfig.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()
            val isPairDialogOpen by viewModel.isPairDialogOpen.collectAsState()
            val isProfileOpen by viewModel.isProfileOpen.collectAsState()
            val session by viewModel.session.collectAsState()
            val isOnline by viewModel.isOnline.collectAsState()

            // Hardware / gesture back handler: return to Home tab if on another screen
            BackHandler(enabled = currentScreen != PncScreen.HOME) {
                viewModel.navigateTo(PncScreen.HOME)
            }

            PncTheme(config = themeConfig) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        PncTopBar(
                            session = session,
                            isOnline = isOnline,
                            onToggleOnline = { viewModel.toggleOnline() },
                            onOpenSettings = { viewModel.openSettings(true) },
                            onOpenPairing = { viewModel.openPairDialog(true) }
                        )
                    },
                    bottomBar = {
                        PncBottomNav(
                            currentScreen = currentScreen,
                            onSelectScreen = { viewModel.navigateTo(it) }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            PncScreen.HOME -> HomeScreen(viewModel = viewModel)
                            PncScreen.CHAT -> ChatScreen(viewModel = viewModel)
                            PncScreen.CALL -> CallScreen(viewModel = viewModel)
                            PncScreen.PLAY -> PlayScreen(viewModel = viewModel)
                            PncScreen.MEMORIES -> MemoriesScreen(viewModel = viewModel)
                        }
                    }

                    // Settings Dialog (Theme Studio & System details)
                    if (isSettingsOpen) {
                        SettingsDialog(
                            viewModel = viewModel,
                            onDismiss = { viewModel.openSettings(false) }
                        )
                    }

                    // Pairing Dialog
                    if (isPairDialogOpen) {
                        PairingDialog(
                            viewModel = viewModel,
                            onDismiss = { viewModel.openPairDialog(false) }
                        )
                    }

                    // Profile & Names Customization Dialog
                    if (isProfileOpen) {
                        ProfileCustomizationDialog(
                            viewModel = viewModel,
                            onDismiss = { viewModel.openProfile(false) }
                        )
                    }
                }
            }
        }
    }
}
