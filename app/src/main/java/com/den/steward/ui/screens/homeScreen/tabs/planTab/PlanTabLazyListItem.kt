// Glory be to LORD our GOD
package com.den.steward.ui.screens.homeScreen.tabs.planTab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.den.steward.backend.entitles.PlanStatus
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.helper.formatedDateTime
import com.den.steward.helper.toLocalDateTime
import com.den.steward.ui.dataAddition.AddPlanFulfillment

@Composable
fun PlanTabLazyListItem(
    transaction: Transaction,
    updateSelectedFulfillmentTransactionType: (TransactionType) -> Unit,
    updateShowFulfillmentTransactionTypeBottomSheet: (Boolean) -> Unit,
    setSelectedTransaction: (Transaction?) -> Unit,
) {
    if (transaction !is Transaction.Plan) return

    val typeColor = colorResource(id = transaction.type.color)
    val statusColor = colorResource(id = transaction.status.color)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* TODO: Navigate to detail */ },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.elevatedCardColors().copy(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(typeColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = transaction.selectedIcon),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = typeColor
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = 0.1f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = transaction.status.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column {
                Text(
                    text = transaction.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = transaction.createdAt.toLocalDateTime().formatedDateTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = transaction.getFormattedAmountOrValue,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = typeColor
                    )

                    AddPlanFulfillment(
                        transaction = transaction,
                        updateSelectedParentTransaction = setSelectedTransaction,
                        updateSelectedFulfillmentTransactionType = updateSelectedFulfillmentTransactionType,
                        updateShowFulfillmentTransactionTypeBottomSheet = updateShowFulfillmentTransactionTypeBottomSheet,
                    ) {
                        setSelectedTransaction(transaction)
                    }
                }
            }
        }
    }
}
