// Grace and truth came through JESUS
package com.den.steward.ui.dataDeletion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.viewModels.DataDeletionViewModel
import com.den.steward.helper.title

@Composable
fun DataDeletionDialog(
    viewModel: DataDeletionViewModel,
    onDismissRequest: () -> Unit,
) {
    val onDialogShowState = viewModel.onDialogShow.collectAsStateWithLifecycle()
    val onDialogShow = onDialogShowState.value

    if (onDialogShow) {
        Dialog(
            onDismissRequest = onDismissRequest
        ) {
            DataDeletionContent(
                viewModel = viewModel,
                onDismissRequest = onDismissRequest
            )
        }
    }
}

@Composable
fun DataDeletionContent(
    viewModel: DataDeletionViewModel,
    onDismissRequest: () -> Unit
) {
    val selectedTransaction = viewModel.selectedTransaction.collectAsStateWithLifecycle()
    val transaction = selectedTransaction.value ?: return

    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.WarningAmber,
                contentDescription = "Warning Icon",
                tint = MaterialTheme.colorScheme.error
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                buildAnnotatedString {
                    append("Delete ")
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    ) {
                        append(transaction.getLabel)
                    }
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = MaterialTheme.typography.titleLarge.fontWeight
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "This action cannot be undone.",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = MaterialTheme.typography.titleSmall.fontWeight
                )
                Text(
                    "Are you sure you want to delete this transaction?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text("Cancel")
                }

                TextButton(
                    onClick = {
                        when(transaction) {
                            is Transaction.Attain,
                            is Transaction.Refund,
                            is Transaction.Repayment,
                            is Transaction.Achievement -> viewModel.deleteFulfillment()
                            else -> viewModel.deleteTransaction()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors().copy(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

