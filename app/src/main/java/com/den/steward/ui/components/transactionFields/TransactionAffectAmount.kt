// Grace and truth came through JESUS
package com.den.steward.ui.components.transactionFields

import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.den.steward.R


@Composable
fun TransactionAffectAmount(
    colorResId: Int,
    modifier: Modifier = Modifier,
    isAffectingAmount: MutableState<Boolean>,
) {
    val color = colorResource(id = colorResId)

    TransactionFieldCard(
        title = "Affect Amount",
        modifier = modifier,
        leadingContent = {
            Image(
                painter = painterResource(R.drawable.stack_of_coins),
                contentDescription = "stack of coins"
            )
        },
        headlineContent = {
            Text(
                text = if (isAffectingAmount.value) "This will affects the amount" else
                    "This will does not affect the amount",
                color = color,
                style = MaterialTheme.typography.labelMedium
            )
        },
        trailingContent = {
            Switch(
                checked = isAffectingAmount.value,
                onCheckedChange = { isAffectingAmount.value = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = color,
                    checkedTrackColor = color.copy(0.4f)
                )
            )
        }
    ){
        isAffectingAmount.value = !isAffectingAmount.value
    }
}
