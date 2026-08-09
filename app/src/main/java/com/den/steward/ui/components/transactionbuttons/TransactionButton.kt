// Love the LORD your GOD with all your soul and with all your mind and with all your heart
// and with all your might and love your neighbor as yourself
package com.den.steward.ui.components.transactionbuttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.den.steward.backend.dataStructure.TransactionType

@Composable
fun TransactionButtons(
    modifier: Modifier = Modifier,
    transactionType: TransactionType,
    isErrors: Boolean = false,
    onClick: () -> Unit
) {

    val desc = when (transactionType) {
        TransactionType.EARNINGS -> "Add your earnings"
        TransactionType.EXPENSE -> "Submit your expense"
        TransactionType.LOAN -> "Request a loan"
        TransactionType.DEBT -> "Borrow money"
        TransactionType.GOAL -> "Set a goal"
        TransactionType.ATTAIN -> "Attain your goal"
        TransactionType.REPAYMENT -> "Make a repayment"
        TransactionType.REFUND -> "Request a refund"
        else -> ""
    }

    val label = when(transactionType) {
        TransactionType.EARNINGS -> "Earned"
        TransactionType.EXPENSE -> "Spent"
        TransactionType.LOAN -> "Lent"
        TransactionType.DEBT -> "Borrowed"
        TransactionType.GOAL -> "Goal"
        TransactionType.ATTAIN -> "Attain"
        TransactionType.REPAYMENT -> "Repayment"
        TransactionType.REFUND -> "Refund"
        else -> ""
    }

    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        border = ButtonDefaults.outlinedButtonBorder().copy(
            brush = if (isErrors) {
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.error,
                        MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                    )
                )
            } else {
                ButtonDefaults.outlinedButtonBorder().brush
            }
        )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                desc,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}