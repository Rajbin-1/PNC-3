package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.PncViewModel
import com.example.ui.theme.LocalPncTheme

@Composable
fun ProfileCustomizationDialog(
    viewModel: PncViewModel,
    onDismiss: () -> Unit
) {
    val theme = LocalPncTheme.current
    val session by viewModel.session.collectAsState()
    val cat by viewModel.cat.collectAsState()

    var userNameInput by remember(session) {
        mutableStateOf(session?.userName ?: "You")
    }
    var partnerNicknameInput by remember(session) {
        mutableStateOf(session?.partnerNickname ?: "Alex")
    }
    var catNameInput by remember(session, cat) {
        mutableStateOf(cat?.name ?: session?.catName ?: "Mochi")
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CUSTOMIZE PROFILES",
                        fontSize = 13.sp,
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

                Spacer(modifier = Modifier.height(18.dp))

                // Field 1: Your Display Name
                Text(
                    text = "Your Display Name",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = userNameInput,
                    onValueChange = {
                        if (it.length <= 24) userNameInput = it
                        errorMessage = null
                    },
                    singleLine = true,
                    placeholder = { Text("e.g. Jordan") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = theme.accentColor)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("user_name_input"),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Field 2: Partner's Nickname (Private on your device)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Partner's Nickname",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Private to your device",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = partnerNicknameInput,
                    onValueChange = {
                        if (it.length <= 24) partnerNicknameInput = it
                        errorMessage = null
                    },
                    singleLine = true,
                    placeholder = { Text("e.g. Alex") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = theme.accentColor)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("partner_nickname_input"),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Field 3: Shared Cat's Name
                Text(
                    text = "Shared Cat Name",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = catNameInput,
                    onValueChange = {
                        if (it.length <= 24) catNameInput = it
                        errorMessage = null
                    },
                    singleLine = true,
                    placeholder = { Text("e.g. Mochi") },
                    leadingIcon = {
                        Icon(Icons.Default.Pets, contentDescription = null, tint = theme.accentColor)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cat_name_input"),
                    shape = RoundedCornerShape(14.dp)
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = errorMessage ?: "",
                        fontSize = 12.sp,
                        color = theme.accentColor
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Save Button
                Button(
                    onClick = {
                        val trimmedUser = userNameInput.trim()
                        val trimmedPartner = partnerNicknameInput.trim()
                        val trimmedCat = catNameInput.trim()

                        if (trimmedUser.isEmpty()) {
                            errorMessage = "Please enter your display name"
                            return@Button
                        }
                        if (trimmedPartner.isEmpty()) {
                            errorMessage = "Please enter partner's nickname"
                            return@Button
                        }
                        if (trimmedCat.isEmpty()) {
                            errorMessage = "Please choose a name for your cat"
                            return@Button
                        }

                        viewModel.updateProfiles(trimmedUser, trimmedPartner, trimmedCat)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_profile_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = theme.accentColor,
                        contentColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = "Save Profile",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
