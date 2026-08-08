// Glory be to LORD our GOD
package com.den.steward.ui.dataAddition

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.den.steward.backend.dataStructure.TransactionType
import com.den.steward.ui.components.bottomDrawerSheet.BottomDrawerSheet
import com.den.steward.ui.components.bottomDrawerSheet.BottomDrawerSheetItem
import com.den.steward.ui.components.bottomDrawerSheet.BottomSheetDataSubmitted
import com.den.steward.ui.components.bottomDrawerSheet.TransactionBottomDrawerSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionAddition(onSubmit: (bottomSheetDataSubmitted: BottomSheetDataSubmitted) -> Unit) {
    val onShow = remember { mutableStateOf(false) }
    val onShowTransaction = remember { mutableStateOf(false) }
    val selectedTransaction = remember { mutableStateOf<TransactionType?>(null) }

    FloatingActionButton(
        onClick = { onShow.value = true }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Transaction"
        )
    }

    // 1. Selection of Transaction Type Bottom Drawer Sheet
    BottomDrawerSheet(
        title = "Transactions",
        description = "Select the type of transaction you're adding",
        show = onShow.value,
        onDismissRequest = { onShow.value = false }
    ) {
        TransactionType.entries.filter {
            it == TransactionType.EARNINGS ||
            it == TransactionType.EXPENSE ||
            it == TransactionType.LOAN ||
            it == TransactionType.DEBT ||
            it == TransactionType.GOAL
        }.forEach { type ->
            BottomDrawerSheetItem(
                title = stringResource(id = type.label),
                description = stringResource(id = type.description),
                icon = {
                    Icon(
                        painter = painterResource(id = type.icon),
                        contentDescription = stringResource(id = type.label),
                        tint = colorResource(id = type.color)
                    )
                }
            ) {
                selectedTransaction.value = type
                onShow.value = false
                onShowTransaction.value = true
            }
        }
    }

    // 2. Show the Transaction Bottom Drawer Sheet
    TransactionBottomDrawerSheet(
        transactionType = selectedTransaction.value ?: TransactionType.EARNINGS,
        show = onShowTransaction.value,
        onDismissRequest = { onShowTransaction.value = false },
        onSubmit = onSubmit
    )
}
