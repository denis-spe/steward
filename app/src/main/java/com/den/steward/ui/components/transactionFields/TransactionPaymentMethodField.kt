// Love the LORD your GOD with all your soul and with all your mind and with all your heart
// and with all your might and love your neighbor as yourself
package com.den.steward.ui.components.transactionFields

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.den.steward.R
import com.den.steward.backend.dataStructure.PaymentMethod
import com.den.steward.helper.title
import kotlin.text.ifEmpty

@Composable
fun TransactionPaymentMethodField(
    colorResId: Int,
    selectedPaymentMethod: MutableState<PaymentMethod>,
) {
   val color = colorResource(id = colorResId)

    val cashBorder = if (selectedPaymentMethod.value == PaymentMethod.CASH) {
        BorderStroke(1.dp, color)
    } else null

    val cardBorder = if (selectedPaymentMethod.value == PaymentMethod.CARD) {
        BorderStroke(1.dp, color)
    } else null

    val cashColor = if (selectedPaymentMethod.value == PaymentMethod.CASH) {
        ColorFilter.tint(color)
    } else null

    val cardColor = if (selectedPaymentMethod.value == PaymentMethod.CARD) {
        ColorFilter.tint(color)
    } else null


    TransactionFieldCard(
        title = "Payment",
        headlineContent = {
            Text(
                text = selectedPaymentMethod.value.label,
                style = MaterialTheme.typography.labelMedium,
            )
        },
        leadingContent = {
            Image(
                painter = painterResource(id = R.drawable.payment),
                contentDescription = selectedPaymentMethod.value.name,
                modifier = Modifier.size(24.dp)
            )
        },
        trailingContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedIconButton(
                    onClick = {
                        selectedPaymentMethod.value = PaymentMethod.CASH
                    },
                    border = cashBorder
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cash),
                        contentDescription = "cash",
                        modifier = Modifier.size(ICON_SIZE),
                    )
                }

                OutlinedIconButton(
                    onClick = {
                        selectedPaymentMethod.value = PaymentMethod.CARD
                    },
                    border = cardBorder
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.credit_card),
                        contentDescription = "credit_card",
                        modifier = Modifier.size(ICON_SIZE),
                    )
                }
            }
        }
    ) {
        selectedPaymentMethod.value = if (selectedPaymentMethod.value == PaymentMethod.CASH)
            PaymentMethod.CARD else PaymentMethod.CASH
    }
}
