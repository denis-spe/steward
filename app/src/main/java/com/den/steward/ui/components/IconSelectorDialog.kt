// Glory Be To The Name of LORD Of Hosts And Lord JESUS
package com.den.steward.ui.components

import androidx.compose.animation.slideIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.den.steward.R

private val icons = mapOf(
    "Apple" to R.drawable.apple,
    "Banana" to R.drawable.banana,
    "Achievement" to R.drawable.achievement,
    "eggs" to R.drawable.eggs
)

data class  SelectedIcon(
    val name: String = "",
    val icon: Int = R.drawable.apple
)

@Composable
fun IconSelectorDialog(
    isShown: Boolean,
    color: Color = MaterialTheme.colorScheme.primary,
    leadingIcon: @Composable () -> Unit = {},
    onDismiss: () -> Unit,
    selectedIcon: SelectedIcon = SelectedIcon(
        name = icons.keys.first(),
        icon = icons.values.first()
    ),
    onIconSelected: (SelectedIcon) -> Unit
) {
    // if not shown, return
    if (!isShown) return

    // Icon selector dialog
    val selectedIcon = remember { mutableStateOf(selectedIcon) }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconSelectorDialogHeader(
                    leadingIcon = leadingIcon
                )

                HorizontalDivider(
                    modifier = Modifier.padding(top = 5.dp)
                )

                IconSelectorDialogBody(
                    defaultSelectedColor = color,
                    selectedIcon = selectedIcon.value
                ) { icon ->
                    selectedIcon.value = icon
                }
                HorizontalDivider()

                IconSelectorDialogFooter(
                    color = color,
                    onDismiss = onDismiss
                ) {
                    onIconSelected(selectedIcon.value)
                }
            }
        }
    }
}

@Composable
private fun IconSelectorDialogHeader(
    leadingIcon: @Composable () -> Unit = {},
) {
    Row (
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon()

        Column (
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Select Icon",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                "Give your transaction a visual identity",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun IconSelectorDialogBody(
    defaultSelectedColor: Color = MaterialTheme.colorScheme.primary,
    selectedIcon: SelectedIcon,
    onIconSelect: (SelectedIcon) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(vertical = 16.dp),
        contentPadding = PaddingValues(5.dp),
        userScrollEnabled = true
    ) {
        icons.forEach { (name, icon) ->
            item(key = name) {
                IconContent(
                    name = name,
                    icon = icon,
                    color = defaultSelectedColor,
                    isSelected = selectedIcon.name == name
                ) {
                    onIconSelect(SelectedIcon(
                        name = name,
                        icon = icon
                    ))
                }
            }
        }
    }
}

@Composable
private fun IconContent(
    name: String,
    icon: Int,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    val border = if (isSelected)
        BorderStroke(1.dp, color)
    else BorderStroke(0.dp, Color.Transparent)

    OutlinedCard (
        shape = MaterialTheme.shapes.medium,
        border = border,
        modifier = Modifier
            .size(80.dp),
        onClick = onClick,
        colors = CardDefaults.outlinedCardColors()
            .copy(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = name,
                modifier = Modifier.size(27.dp)
            )

            Text(
                name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun IconSelectorDialogFooter(
    color: Color,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        TextButton(
            onClick = onDismiss
        ) {
            Text(
                "Cancel",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.error
                )
            )
        }

        Button(
            onClick = {
                onConfirm()
                onDismiss()
            },
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = color
            )
        ) {
            Text(
                "Done",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}