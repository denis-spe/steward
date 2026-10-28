package com.den.steward.ui.screens.settingsScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import coil.compose.AsyncImage
import com.den.steward.R
import com.den.steward.backend.states.AuthState
import com.den.steward.backend.viewModels.SettingsViewModel
import com.den.steward.helper.pop
import com.den.steward.ui.componentExtenison.DottedHorizontalDivider
import com.den.steward.ui.screens.screenManager.WelcomeRouter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(backStack: NavBackStack<NavKey>, settingsViewModel: SettingsViewModel) {
    val userState by settingsViewModel.userState.collectAsStateWithLifecycle()

    if (userState is AuthState.NotAuthenticated) {
        backStack.clear()
        backStack.add(WelcomeRouter)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { backStack.pop() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SettingsScreenHeader(userState)

            SettingsScreenAccount(
                onLogout = {
                    settingsViewModel.logout()
                }
            )
        }
    }
}

@Composable
private fun SettingsScreenHeader(
    userState: AuthState
) {
    if (userState !is AuthState.Authenticated) return

    val state = remember(userState) {
        object {
            val names = if (userState.isAnonymous) "Anonymous Guest" else
                userState.displayName ?: "Unknown User"
            val email = userState.email
            val photoUrl = userState.photoUrl
        }
    }

    val imageSize = 100.dp

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            modifier = Modifier.padding(bottom = 16.dp),
            shape = CircleShape,
            shadowElevation = 6.dp,
            color = MaterialTheme.colorScheme.surfaceVariant // Gives a background to default icon
        ) {
            state.photoUrl?.let {
                AsyncImage(
                    model = it,
                    contentDescription = null,
                    modifier = Modifier
                        .size(imageSize)
                        .clip(CircleShape)
                )
            } ?: Image(
                painter = painterResource(R.drawable.default_user),
                contentDescription = null,
                modifier = Modifier
                    .size(imageSize)
                    .clip(CircleShape),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                state.names,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            if (!state.email.isNullOrEmpty()) {
                Text(
                    state.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                TextButton(
                    onClick = { /*TODO*/ },
                ) {
                    Text(
                        "Sync to save your changes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsScreenAccount(
    onPasswordChange: () -> Unit = {},
    onEmailChange: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SettingsScreenSectionPartHeader(
            title = "Account"
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
        }

        SettingsScreenSectionPartRow(
            title = "Password",
            description = "Change your password",
            onPasswordChange
        )

        SettingsScreenSectionPartRow(
            title = "Email",
            description = "Change your email",
            onEmailChange
        )

        SettingsScreenSectionPartRow(
            title = "Delete Account",
            description = "Delete your account",
            onDeleteAccount
        )

        SettingsScreenSectionPartRow(
            title = "Log out",
            description = "Sign out of your account",
            onLogout
        )
    }
}

@Composable
private fun SettingsScreenSectionPartHeader(
    title: String,
    icon: @Composable () -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )

        DottedHorizontalDivider(
            modifier = Modifier.weight(1f),
        )

        icon()
    }
}

@Composable
private fun SettingsScreenSectionPartRow(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.small,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
            )
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

}