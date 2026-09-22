// Love the LORD your GOD with all your heart and with all your soul
// and with all your might and love your neighbor as your self
package com.den.steward.ui.dataAddition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.entitles.PlanStatus
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataState
import com.den.steward.backend.viewModels.DataAdditionViewModel
import com.den.steward.backend.viewModels.PlanTabViewModel
import com.den.steward.ui.components.bottomDrawerSheet.BottomDrawerSheet
import com.den.steward.ui.components.transactionFields.PlanFulfillmentAmountField
import com.den.steward.ui.components.transactionFields.PlanFulfillmentLabelField
import com.den.steward.ui.components.transactionFields.TransactionFieldState

@Composable
fun AddPlanFulfillment(
    transaction: Transaction,
    modifier: Modifier = Modifier,
    dataAdditionViewModel: DataAdditionViewModel,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = {
            onClick()
            dataAdditionViewModel.updateSelectedFulfillmentTransactionType(TransactionType.PLAN_FULFILLMENT)
            dataAdditionViewModel.updateSelectedParentTransaction(transaction)
            dataAdditionViewModel.updateShowFulfillmentTransactionTypeBottomSheet(true)
        },
        modifier = modifier,
    ) {
        Text(
            "Add",
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
fun PlanFulfillmentBottomDrawerSheet(
    planTabViewModel: PlanTabViewModel,
    dataAdditionViewModel: DataAdditionViewModel
) {
    val dataAdditionState by dataAdditionViewModel.dataAdditionState.collectAsStateWithLifecycle()
    val planFulfillmentState by planTabViewModel.planFulfillmentTransactions.collectAsStateWithLifecycle()

    BottomDrawerSheet(
        title = "Plan fulfillment",
        description = "Add, update or delete plan fulfillment",
        show = dataAdditionState.showFulfillmentTransactionTypeBottomSheet,
        isScrollable = false,
        onDismissRequest = {
            dataAdditionViewModel.updateSelectedParentTransaction(null)
            planTabViewModel.setSelectedTransaction(null)
            dataAdditionViewModel.updateShowFulfillmentTransactionTypeBottomSheet(false)
        },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                when (val state = planFulfillmentState) {
                    is DataState.Loading -> {
                        item(
                            key = "loading",
                            contentType = "loading"
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    is DataState.Error -> {
                        item(
                            key = "error",
                            contentType = "error"
                        ) {
                            Text(
                                text = "Error: ${state.message}",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    is DataState.Success -> {
                        val transactions = state.data

                        if (transactions.isNotEmpty()) {
                            items(
                                transactions.size,
                                key = { index -> transactions[index].id },
                                contentType = { index -> transactions[index].id }
                            ) { index ->
                                val item = transactions[index] as Transaction.PlanFulfillment
                                PlanFulfillmentBottomDrawerSheetItem(
                                    label = item.getLabel,
                                    amount = item.getFormattedAmountOrValue,
                                    statusName = item.getStatus,
                                    fulfillmentType = item.fulfillmentType,
                                    note = item.getNote,
                                    onStatusChange = {

                                    },
                                    onDelete = {

                                    },
                                    onEdit = {

                                    }
                                )
                            }
                        } else {
                            item(
                                key = "empty",
                                contentType = "empty"
                            ) {
                                Text(
                                    text = "No transactions found",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                item(
                    key = "add",
                    contentType = "add"
                ) {
                    PlanFulfillmentForm(
                        dataAdditionViewModel = dataAdditionViewModel
                    )
                }
            }

        }
    }
}


@Composable
fun PlanFulfillmentBottomDrawerSheetItem(
    modifier: Modifier = Modifier,
    label: String,
    amount: String,
    statusName: String?,
    fulfillmentType: TransactionType,
    note: String? = null,
    onStatusChange: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {

    val status = when (statusName) {
        "PENDING" -> PlanStatus.PENDING
        "ACHIEVED" -> PlanStatus.ACHIEVED
        "FAILED" -> PlanStatus.FAILED
        else -> PlanStatus.NOT_YET
    }

    val statusIcon = remember {
        when (status) {
            PlanStatus.PENDING -> Icons.Default.RemoveCircleOutline
            PlanStatus.ACHIEVED -> Icons.Default.CheckCircleOutline
            PlanStatus.FAILED -> Icons.Default.Cancel
            PlanStatus.NOT_YET -> Icons.Default.RemoveCircleOutline
        }
    }

    val material = MaterialTheme.colorScheme

    val statusColor = remember {
        when (status) {
            PlanStatus.NOT_YET -> material.onSurface
            PlanStatus.ACHIEVED -> Color(0xFF16C210)
            PlanStatus.FAILED -> Color(0xFFD00000)
            PlanStatus.PENDING -> Color(0xFFF5A623)
        }
    }

    val onExpand = remember {
        mutableStateOf(false)
    }

    val transactionTypeColor = colorResource(TransactionType.PLAN_FULFILLMENT.color)
    val fulfillmentTypeColor = colorResource(fulfillmentType.color)


        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(fulfillmentTypeColor),
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        label,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        amount,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = fulfillmentTypeColor
                    )

                    Spacer(modifier = Modifier.width(2.dp))

                    IconButton(
                        onClick = onStatusChange,
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = "status",
                            tint = statusColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    IconButton(
                        onClick = {
                            onExpand.value = !onExpand.value
                        },
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "extend",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = onExpand.value,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (note != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                note,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(
                            onClick = onEdit,
                            modifier = Modifier
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "edit",
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(end = 4.dp)
                            )

                            Text(
                                "Edit",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        TextButton(
                            onClick = onDelete,
                            modifier = Modifier
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "delete",
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(end = 4.dp)
                            )

                            Text(
                                "Delete",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
}


@Composable
fun ShowAddPlanFulfillmentForm(
    modifier: Modifier = Modifier,
    onShow: () -> Unit,
) {
    FilledIconButton(
        onClick = onShow,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add"
        )
    }
}

@Composable
fun AddPlanFulfillmentBtn(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add"
        )
    }
}


@Composable
fun PlanFulfillmentForm(
    dataAdditionViewModel: DataAdditionViewModel
) {
    val dataAdditionState by dataAdditionViewModel.dataAdditionState.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PlanFulfillmentLabelField(
            state = dataAdditionState.label,
            placeholder = "Label",
            isError = dataAdditionState.isLabelCorrect is TransactionFieldState.Error,
            modifier = Modifier.weight(1f)
        )

        PlanFulfillmentAmountField(
            state = dataAdditionState.amount,
            placeholder = "0.0",
            isError = dataAdditionState.isAmountCorrect is TransactionFieldState.Error,
            modifier = Modifier.weight(0.6f)
        )

        AddPlanFulfillmentBtn(
            onClick = dataAdditionViewModel::addPlanFulfillment
        )
    }
}




