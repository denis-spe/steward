// Love the LORD your GOD with all your soul and with all your mind and with all your heart
// and with all your might and love your neighbor as yourself
package com.den.steward.ui.components.transactionbuttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.ui.theme.ExtendedTheme

@Composable
fun TransactionButtons(
    colorResId: Int,
    modifier: Modifier = Modifier,
    transactionType: TransactionType,
    isErrors: Boolean = false,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {

    val color = colorResource(id = colorResId)

    val desc = when (transactionType) {
        TransactionType.EARNINGS -> "Add your earnings"
        TransactionType.EXPENSE -> "Submit your expense"
        TransactionType.LENT -> "Request a lent"
        TransactionType.DEBT -> "Borrow money"
        TransactionType.GOAL -> "Set a goal"
        TransactionType.ATTAIN -> "Attain your goal"
        TransactionType.REPAYMENT -> "Make a repayment"
        TransactionType.SETTLEMENT -> "Submit a settlement"
        TransactionType.SAVINGS -> "Record your money"
        TransactionType.PLAN -> "Plan for your future"
        else -> ""
    }

    val label = when(transactionType) {
        TransactionType.EARNINGS -> "Earned"
        TransactionType.EXPENSE -> "Spent"
        TransactionType.LENT -> "Lent"
        TransactionType.DEBT -> "Borrowed"
        TransactionType.GOAL -> "Goal"
        TransactionType.ATTAIN -> "Attain"
        TransactionType.REPAYMENT -> "Repayment"
        TransactionType.SETTLEMENT -> "Settlement"
        TransactionType.SAVINGS -> "Save"
        TransactionType.PLAN -> "Plan"
        else -> ""
    }

    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        enabled = !isLoading,
        border = BorderStroke(2.dp, if (isErrors)
            MaterialTheme.colorScheme.error else color)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                Text(
                    "Submitting...",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    desc,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
fun TransactionUpdateButtons(
    colorResId: Int,
    modifier: Modifier = Modifier,
    transactionType: TransactionType,
    isErrors: Boolean = false,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {

    val color = colorResource(id = colorResId)

    val desc = when (transactionType) {
        TransactionType.EARNINGS -> "Update your earnings"
        TransactionType.EXPENSE -> "Update your expense"
        TransactionType.LENT -> "Update a lent"
        TransactionType.DEBT -> "Update your debt"
        TransactionType.GOAL -> "Update a goal"
        TransactionType.ATTAIN -> "Update your goal"
        TransactionType.REPAYMENT -> "Update your repayment"
        TransactionType.SETTLEMENT -> "Update settlement"
        TransactionType.SAVINGS -> "Update your savings"
        TransactionType.PLAN -> "Update your plan"
        else -> ""
    }

    val label = when(transactionType) {
        TransactionType.EARNINGS -> "Earned"
        TransactionType.EXPENSE -> "Spent"
        TransactionType.LENT -> "Lent"
        TransactionType.DEBT -> "Borrowed"
        TransactionType.GOAL -> "Goal"
        TransactionType.ATTAIN -> "Attain"
        TransactionType.REPAYMENT -> "Repayment"
        TransactionType.SETTLEMENT -> "Settlement"
        TransactionType.SAVINGS -> "Save"
        TransactionType.PLAN -> "Plan"
        else -> ""
    }

    Button(
        modifier = modifier.fillMaxWidth(0.8f),
        onClick = onClick,
        enabled = !isLoading,
        border = BorderStroke(2.dp, if (isErrors)
            MaterialTheme.colorScheme.error else color),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White
        )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                Text(
                    "Submitting...",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    desc,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}