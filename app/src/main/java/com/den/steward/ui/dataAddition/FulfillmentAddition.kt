// Glory be to LORD GOD oof hosts
package com.den.steward.ui.dataAddition

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.ui.components.bottomDrawerSheet.BottomDrawerSheet
import com.den.steward.ui.components.bottomDrawerSheet.BottomDrawerSheetItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FulfillmentAddition() {
    val onShow = remember { mutableStateOf(false) }

    FloatingActionButton(
        onClick = { onShow.value = true }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Fulfillment"
        )
    }

    BottomDrawerSheet(
        title = "Fulfillment",
        description = "Choose the type of fulfillment you're adding",
        show = onShow.value,
        onDismissRequest = { onShow.value = false },
    ) {
        TransactionType.entries.filter {
            it == TransactionType.REPAYMENT ||
                    it == TransactionType.REFUND ||
                    it == TransactionType.ATTAIN
        }.forEach { type ->
            BottomDrawerSheetItem(
                title = stringResource(id = type.label),
                description = stringResource(id = type.description),
                icon = {
                    Icon(
                        painter = painterResource(id = type.icon),
                        contentDescription = stringResource(id = type.label),
                        tint = Color.Unspecified
                    )
                },
                onClick = {

                },
            )
        }
    }
}