// Grace and truth came through JESUS CHRIST
package com.den.steward.ui.screens.homeScreen.tabs.allTab

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.den.steward.backend.entitles.Transaction
import com.den.steward.helper.formattedTime
import com.den.steward.helper.toLocalDateTime
import com.den.steward.ui.theme.ExtendedTheme

@Composable
fun AllTabLazyListStickyHeader(date: String) {
    Surface(
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                date,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AllTabLazyListItem(
    transaction: Transaction,
    modifier: Modifier,
    shape: Shape
) {
    val iconSize = 20.dp

    val formattedTime = remember(transaction.createdAt) {
        transaction.createdAt.toLocalDateTime().formattedTime
    }
    
    val status = remember(transaction) {
        transaction.getStatus
    }
    
    val label = remember(transaction) {
        transaction.getLabel
    }
    
    val amountOrValue = remember(transaction) {
        transaction.getFormattedAmountOrValue
    }

   Surface(
       modifier = modifier,
       shape = shape,
       shadowElevation = 3.dp,
       color = ExtendedTheme.colors.lightPrimary
   ) {
       Row(
           modifier = Modifier.fillMaxWidth()
               .padding(
                   vertical = 16.dp,
                   horizontal = 5.dp
               ),
           horizontalArrangement = Arrangement.SpaceBetween,
           verticalAlignment = Alignment.CenterVertically
       ) {

           Column(
               horizontalAlignment = Alignment.Start,
               verticalArrangement = Arrangement.Center
           ) {
               Row(
                   horizontalArrangement = Arrangement.spacedBy(4.dp),
                   verticalAlignment = Alignment.CenterVertically
               ) {
                   Box(
                       modifier = Modifier
                           .clip(CircleShape)
                           .background(
                               color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                           )
                   ) {
                       Box(
                           modifier = Modifier
                               .size(25.dp)
                               .padding(2.dp)
                       ) {
                           Icon(
                               painter = painterResource(id = transaction.type.icon),
                               contentDescription = stringResource(id = transaction.type.label),
                               tint = colorResource(transaction.type.color),
                               modifier = Modifier.size(iconSize)
                           )
                       }
                   }

                   Column(
                       horizontalAlignment = Alignment.Start,
                       verticalArrangement = Arrangement.Center
                   ) {

                       status?.let {
                           Text(
                               it,
                               style = MaterialTheme.typography.bodySmall,
                               fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                               color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                           )
                       }

                       Text(
                           label,
                           style = MaterialTheme.typography.bodyMedium,
                           fontWeight = MaterialTheme.typography.bodyMedium.fontWeight
                       )
                       Spacer(modifier = Modifier.size(4.dp))
                       Text(
                           formattedTime,
                           style = MaterialTheme.typography.bodySmall,
                           fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
                           color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                       )
                   }
               }
           }

           Column(
               horizontalAlignment = Alignment.End,
               verticalArrangement = Arrangement.Center
           ) {
               Text(
                   amountOrValue,
                   style = MaterialTheme.typography.labelLarge,
                   fontWeight = MaterialTheme.typography.labelLarge.fontWeight
               )
               Spacer(modifier = Modifier.size(4.dp))
               Row(
                   horizontalArrangement = Arrangement.spacedBy(4.dp),
                   verticalAlignment = Alignment.CenterVertically
               ) {
                   Image(
                       painter = painterResource(id = transaction.getPaymentMethodOrNull?.icon ?: transaction.type.icon),
                       contentDescription = stringResource(id = transaction.type.label),
                       modifier = Modifier.size(iconSize)
                   )
               }
           }
       }
   }
}

@Composable
fun AllTabLazyListItemShimmer() {

}