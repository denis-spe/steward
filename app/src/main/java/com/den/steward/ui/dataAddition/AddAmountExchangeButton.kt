// Glory be to the name LORD GOD
package com.den.steward.ui.dataAddition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChangeCircle
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.viewModels.DataAdditionViewModel
import com.den.steward.ui.components.bottomDrawerSheet.BottomDrawerSheet
import com.den.steward.ui.components.transactionFields.ExchangeAmountField
import com.den.steward.ui.components.transactionFields.TransactionFieldState
import com.den.steward.ui.components.transactionbuttons.ExchangeAccountButton
import com.den.steward.ui.components.transactionbuttons.TransactionButtons

@Composable
fun AddAmountExchangeButton(
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    dataAdditionViewModel: DataAdditionViewModel
) {
    // The state of data addition for textField, validation and button state
    val dataAdditionState by dataAdditionViewModel.dataAdditionState.collectAsStateWithLifecycle()

    val fromPayment = dataAdditionState.fromPayment.label
    val toPayment = dataAdditionState.toPayment.label
    val fromPaymentIcon = dataAdditionState.fromPayment.icon
    val toPaymentIcon = dataAdditionState.toPayment.icon
    val type = TransactionType.EXCHANGE

    FloatingActionButton(
        onClick = {
            dataAdditionViewModel.updateShowRampingBottomSheet(true)
        },
        shape = shape,
        elevation = elevation,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.onSecondary
    ) {
        Icon(
            imageVector = Icons.Outlined.ChangeCircle,
            contentDescription = "Amount Exchange"
        )
    }

    LaunchedEffect(dataAdditionState.amount.text) {
        dataAdditionViewModel.updateIsAmountCorrect(TransactionFieldState.Initial)
    }

    // 1. Selection of Transaction Type Bottom Drawer Sheet
    BottomDrawerSheet(
        title = "Exchange",
        description = if (dataAdditionState.isAmountCorrect is TransactionFieldState.Error)
            (dataAdditionState.isAmountCorrect as TransactionFieldState.Error).message
        else
            "Turn amount from credit card or bank into cash",
        show = dataAdditionState.showRampingBottomSheet,
        onDismissRequest = { dataAdditionViewModel.updateShowRampingBottomSheet(false) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ExchangeAmountField(
                dataAdditionState.amount
            )

            ExchangeAccountButton(
                fromPayment = fromPayment,
                toPayment = toPayment,
                fromPaymentIcon = fromPaymentIcon,
                toPaymentIcon = toPaymentIcon,
                onClick = dataAdditionViewModel::onExchange
            )

            TransactionButtons(
                colorResId = type.color,
                modifier = Modifier.padding(vertical = 16.dp),
                transactionType = type,
                isErrors = dataAdditionState.isAmountCorrect is TransactionFieldState.Error,
                isLoading = dataAdditionState.isSaving,
                onClick = dataAdditionViewModel::addRampingTransaction
            )
        }
    }
}