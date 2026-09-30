// Bless be the name of the LORD
package com.den.steward.ui.components.transactionFields

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.den.steward.R
import com.den.steward.backend.entitles.PaymentMethod
import com.den.steward.ui.components.IconSelectorDialog
import com.den.steward.ui.components.SelectedIcon

@Composable
fun TransactionIconSelector(
    selectedIcon: SelectedIcon = SelectedIcon(),
    leadingIcon: @Composable () -> Unit = {},
    onIconSelected: (SelectedIcon) -> Unit
) {
    val onShow = remember { mutableStateOf(false) }
    val selectedIcon = remember { mutableStateOf(selectedIcon) }

    TransactionFieldCard(
        title = "Select Icon",
        leadingContent = {
            Image(
                painter = painterResource(id = R.drawable.description),
                contentDescription = "payment",
                modifier = Modifier.size(24.dp)
            )
        },
        trailingContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = selectedIcon.value.icon),
                    contentDescription = selectedIcon.value.name,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = selectedIcon.value.name,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    ) {
        onShow.value = true
    }

    IconSelectorDialog(
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