// Glory be to the name LORD GOD
package com.den.steward.ui.dataAddition

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.outlined.ChangeCircle
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataAdditionState
import com.den.steward.backend.viewModels.DataAdditionViewModel
import com.den.steward.helper.getCurrencySymbol
import com.den.steward.ui.components.bottomDrawerSheet.BottomDrawerSheet
import com.den.steward.ui.components.bottomDrawerSheet.BottomDrawerSheetItem
import com.den.steward.ui.components.transactionFields.AMOUNT_FONT_SIZE
import com.den.steward.ui.components.transactionFields.CustomInputTransformation
import com.den.steward.ui.components.transactionFields.CustomOutputTransformation
import com.den.steward.ui.components.transactionFields.ExchangeAmountField
import com.den.steward.ui.components.transactionFields.TransactionFieldState
import com.den.steward.ui.components.transactionbuttons.ExchangeAccountButton
import com.den.steward.ui.components.transactionbuttons.TransactionButtons
import com.den.steward.ui.theme.ExtendedTheme

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
    val type = TransactionType.RAMPING

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
        title = "Ramping",
        description = if (dataAdditionState.isAmountCorrect is TransactionFieldState.Error)
            (dataAdditionState.isAmountCorrect as TransactionFieldState.Error).message
        else
            "Turn amount from credit card or bank into cash",
        show = dataAdditionState.showRampingBottomSheet,
        onDismissRequest = { dataAdditionViewModel.updateShowRampingBottomSheet(false) },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
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