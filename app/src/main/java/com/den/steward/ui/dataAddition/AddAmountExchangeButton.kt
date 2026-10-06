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
import com.den.steward.backend.states.DataAdditionState
import com.den.steward.backend.viewModels.DataAdditionViewModel
import com.den.steward.helper.getCurrencySymbol
import com.den.steward.ui.components.bottomDrawerSheet.BottomDrawerSheet
import com.den.steward.ui.components.bottomDrawerSheet.BottomDrawerSheetItem
import com.den.steward.ui.components.transactionFields.AMOUNT_FONT_SIZE
import com.den.steward.ui.components.transactionFields.CustomInputTransformation
import com.den.steward.ui.components.transactionFields.CustomOutputTransformation
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
    val symbol = getCurrencySymbol()

    FloatingActionButton(
        onClick = { dataAdditionViewModel.updateShowRampingBottomSheet(true) },
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

    // 1. Selection of Transaction Type Bottom Drawer Sheet
    BottomDrawerSheet(
        title = "Ramping",
        description = "Turn amount from credit card or bank into cash",
        show = dataAdditionState.showRampingBottomSheet,
        onDismissRequest = { dataAdditionViewModel.updateShowRampingBottomSheet(false) },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(0.8f),
                shape = CircleShape,
                shadowElevation = 2.dp
            ) {
                TextField(
                    state = dataAdditionState.amount,
                    colors = TextFieldDefaults.colors().copy(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    textStyle = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = AMOUNT_FONT_SIZE
                    ),
                    placeholder = {
                        Text(
                            text = "0.0",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.5f
                                ),
                                fontSize = AMOUNT_FONT_SIZE
                            )
                        )
                    },

                    leadingIcon = {
                        Text(
                            text = symbol,
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.5f
                            ),
                            fontWeight = FontWeight.Bold,
                            fontSize = AMOUNT_FONT_SIZE
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    inputTransformation = CustomInputTransformation(),
                    outputTransformation = CustomOutputTransformation(),
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(0.8f),
                shape = CircleShape,
                shadowElevation = 2.dp,
                onClick = dataAdditionViewModel::onExchange
            ) {
                Row(
                    modifier = Modifier.padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = fromPaymentIcon),
                            contentDescription = fromPayment,
                            modifier = Modifier.size(24.dp)
                        )

                        Text(
                            fromPayment,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.SyncAlt,
                        contentDescription = "Sync"
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = toPaymentIcon),
                            contentDescription = toPayment,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            toPayment,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}