// Bless be the name of the LORD
package com.den.steward.ui.components.transactionFields

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.den.steward.R
import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.ui.components.IconSelectorDialog
import com.den.steward.ui.components.SelectedIcon

@Composable
fun TransactionIconSelector(
    colorResId: Int,
    selectedIcon: SelectedIcon = SelectedIcon(),
    leadingIcon: @Composable () -> Unit = {},
    onIconSelected: (SelectedIcon) -> Unit
) {
    val onShow = remember { mutableStateOf(false) }
    val selectedIcon = remember { mutableStateOf(selectedIcon) }
    val color = colorResource(colorResId)

    TransactionFieldCard(
        title = "Choose Icon",
        leadingContent = {
            Image(
                painter = painterResource(id = R.drawable.tag),
                contentDescription = "tag",
                modifier = Modifier.size(24.dp)
            )
        },
        trailingContent = {
            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = selectedIcon.value.icon),
                    contentDescription = selectedIcon.value.name,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = selectedIcon.value.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = color
                )
            }
        }
    ) {
        onShow.value = true
    }

    IconSelectorDialog(
        color = color,
        isShown = onShow.value,
        onIconSelected = {
            selectedIcon.value = it
            onIconSelected(it)
        },
        selectedIcon = selectedIcon.value,
        leadingIcon = leadingIcon,
        onDismiss = { onShow.value = false }
    )
}