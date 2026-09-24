// Love the LORD your GOD with all your heart and with all your soul
// and with all your might and love your neighbor as your self
package com.den.steward.ui.dataAddition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateBounds
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.den.steward.backend.entitles.PlanStatus
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.backend.states.DataAdditionState
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.PlanTabUiState
import com.den.steward.ui.components.bottomDrawerSheet.BottomDrawerSheet
import com.den.steward.ui.components.transactionFields.PlanFulfillmentAmountField
import com.den.steward.ui.components.transactionFields.PlanFulfillmentLabelField
import com.den.steward.ui.components.transactionFields.TransactionFieldState
import com.den.steward.ui.components.transactionFields.TransactionTypeSelector

@Composable
fun AddPlanFulfillment(
    transaction: Transaction,
    modifier: Modifier = Modifier,
    updateSelectedFulfillmentTransactionType: (TransactionType) -> Unit,
    updateSelectedParentTransaction: (Transaction?) -> Unit,
    updateShowFulfillmentTransactionTypeBottomSheet: (Boolean) -> Unit,
    onClick: () -> Unit = {},
) {
    Button(
        onClick = {
            onClick()
            updateSelectedFulfillmentTransactionType(TransactionType.PLAN_FULFILLMENT)
            updateSelectedParentTransaction(transaction)
            updateShowFulfillmentTransactionTypeBottomSheet(true)
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
    planTabUiState: PlanTabUiState,
    planFulfillmentState: DataState<List<Transaction>>,
    setSelectedTransaction: (transaction: Transaction?) -> Unit,
    updateShowFulfillmentTransactionTypeBottomSheet: (Boolean) -> Unit,
    addPlanFulfillment: () -> Unit,
    onTypeChange: (TransactionType) -> Unit,
    updatePlanFulfillmentStatus: (
        transactionId: String,
        status: PlanStatus,
        planFulfillment: Transaction.PlanFulfillment) -> Unit,
    deleteFulfillmentPlan: (transactionId: String, transaction: Transaction) -> Unit
) {


    BottomDrawerSheet(
        title = "Plan fulfillment",
        description = "Add, update or delete plan fulfillment",
        show = planTabUiState.showFulfillmentTransactionTypeBottomSheet,
        isScrollable = false,
        onDismissRequest = {
            setSelectedTransaction(null)
            updateShowFulfillmentTransactionTypeBottomSheet(false)
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
                when (planFulfillmentState) {
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
                                text = "Error: ${planFulfillmentState.message}",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    is DataState.Success -> {
                        val transactions = planFulfillmentState.data

                        if (transactions.isNotEmpty()) {
                            items(
                                transactions.size,
                                key = { index -> transactions[index].id },
                                contentType = { index -> transactions[index].id }
                            ) { index ->
                                val item = transactions[index] as Transaction.PlanFulfillment
                                PlanFulfillmentBottomDrawerSheetItem(
                                    modifier = Modifier.animateItem(),
                                    planFulfillment = item,
                                    onStatusChange = updatePlanFulfillmentStatus,
                                    onDelete = {
                                        deleteFulfillmentPlan(
                                            item.plan.id,
                                            item
                                        )
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
                        planTabUiState = planTabUiState,
                        addPlanFulfillment = addPlanFulfillment,
                        onTypeChange = onTypeChange
                    )
                }
            }

        }
    }
}


@Composable
fun PlanFulfillmentBottomDrawerSheetItem(
    modifier: Modifier = Modifier,
    planFulfillment: Transaction.PlanFulfillment,
    onStatusChange: (
        planId: String,
        status: PlanStatus,
        fulfillment: Transaction.PlanFulfillment) -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
) {

    val status = planFulfillment.status

    val statusIcon = when (status) {
        PlanStatus.PENDING -> Icons.Default.Restore
        PlanStatus.ACHIEVED -> Icons.Default.CheckCircleOutline
        PlanStatus.FAILED -> Icons.Default.Cancel
        PlanStatus.NOT_YET -> Icons.Default.RemoveCircleOutline
    }

    val material = MaterialTheme.colorScheme

    val statusColor = when (status) {
        PlanStatus.NOT_YET -> material.onSurface
        PlanStatus.ACHIEVED -> Color(0xFF16C210)
        PlanStatus.FAILED -> Color(0xFFD00000)
        PlanStatus.PENDING -> Color(0xFFF5A623)
    }

    val onExpand = remember {
        mutableStateOf(false)
    }

    val expandIcon = if (onExpand.value) Icons.Default.KeyboardArrowUp
    else Icons.Default.KeyboardArrowDown

    val fulfillmentType = planFulfillment.fulfillmentType
    val fulfillmentTypeColor = colorResource(fulfillmentType.color)
    val label = planFulfillment.label
    val amount = planFulfillment.getFormattedAmountOrValue
    val note = planFulfillment.note
    val planId = planFulfillment.plan.id

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = painterResource(fulfillmentType.icon),
                    contentDescription = "fulfillment type",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(fulfillmentTypeColor)
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
                    onClick = {

                        val planStatusState = when (status) {
                            PlanStatus.PENDING -> PlanStatus.ACHIEVED
                            PlanStatus.ACHIEVED -> PlanStatus.FAILED
                            PlanStatus.FAILED -> PlanStatus.PENDING
                            PlanStatus.NOT_YET -> PlanStatus.PENDING
                        }

                        onStatusChange(
                            planId,
                            planStatusState,
                            planFulfillment
                        )
                    },
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
                        imageVector = expandIcon,
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
                if (note.isNotEmpty()) {
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
    planTabUiState: PlanTabUiState,
    addPlanFulfillment: () -> Unit,
    onTypeChange: (TransactionType) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PlanFulfillmentLabelField(
                state = planTabUiState.label,
                placeholder = "Label",
                isError = planTabUiState.isLabelCorrect is TransactionFieldState.Error,
                modifier = Modifier.weight(1f)
            )

            PlanFulfillmentAmountField(
                state = planTabUiState.amount,
                placeholder = "0.0",
                isError = planTabUiState.isAmountCorrect is TransactionFieldState.Error,
                modifier = Modifier.weight(0.6f)
            )

            AddPlanFulfillmentBtn(
                onClick = addPlanFulfillment
            )
        }

        TransactionTypeSelector(
            selectedType = planTabUiState.selectedPlanFulfillmentType,
            onTypeChange = onTypeChange,
        )
    }
}




