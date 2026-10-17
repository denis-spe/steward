// Glory be to name of the LORD GOD
package com.den.steward.ui.screens.homeScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.den.steward.R
import com.den.steward.backend.states.AuthState
import com.den.steward.ui.theme.ExtendedTheme

@Composable
fun HomeTopDrawer(
    userState: AuthState,
    isOpen: Boolean,
    onClose: () -> Unit,
    onSettingsClick: () -> Unit,
    onHomeClick: () -> Unit
) {

    val state = remember(userState) {
        object {
            val name = if (userState is AuthState.Authenticated) {
                if (userState.isAnonymous) "Guest" else
                    userState.displayName ?: "Guest"
            } else null

            val image = if (userState is AuthState.Authenticated) {
                userState.photoUrl
            } else null
        }
    }


    if (isOpen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.4f))
                .clickable { onClose() },
        )
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background.copy(0.95f),
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            shape = RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.padding(bottom = 16.dp),
                    shape = CircleShape,
                    shadowElevation = 6.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant // Gives a background to default icon
                ) {
                    state.image?.let { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Crop
                        )
                    } ?: Image(
                        painter = painterResource(R.drawable.default_user),
                        contentDescription = "Default Profile Picture",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(16.dp), // Inner padding so default icon sits nicely inside the circle
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                state.name?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                Row(
                    modifier = Modifier.padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    FilledIconButton(
                        onClick = onHomeClick,
                        colors = IconButtonDefaults.filledIconButtonColors().copy(
                            containerColor = ExtendedTheme.colors.lightGray,
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Home,
                            contentDescription = "Accessibility",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    FilledIconButton(
                        onClick = onSettingsClick,
                        colors = IconButtonDefaults.filledIconButtonColors().copy(
                            containerColor = ExtendedTheme.colors.lightGray,
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Accessibility",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    }
}