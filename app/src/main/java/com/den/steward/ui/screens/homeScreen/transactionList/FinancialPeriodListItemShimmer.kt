// Bless be the LORD GOD of host and he who came in the name of the LORD
package com.den.steward.ui.screens.homeScreen.transactionList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.den.steward.ui.componentExtenison.shimmerEffect

@Composable
fun FinancialPeriodListItemShimmer() {
    Surface {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {

                // Label shimmer
                Box(
                    modifier = Modifier
                        .size(60.dp, 16.dp)
                        .clip(MaterialTheme.shapes.small)
                        .shimmerEffect()
                )

                Spacer(modifier = Modifier.size(8.dp))

                // Description shimmer
                Box(
                    modifier = Modifier
                        .size(100.dp, 16.dp)
                        .clip(MaterialTheme.shapes.small)
                        .shimmerEffect()
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                // Amount shimmer
                Box(
                    modifier = Modifier
                        .size(100.dp, 16.dp)
                        .clip(MaterialTheme.shapes.small)
                        .shimmerEffect()
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    // Payment Method shimmer
                    Box(
                        modifier = Modifier
                            .size(16.dp, 16.dp)
                            .clip(CircleShape)
                            .shimmerEffect()
                    )

                    Spacer(modifier = Modifier.size(4.dp))

                    // Transaction Type shimmer
                    Box(
                        modifier = Modifier
                            .size(16.dp, 16.dp)
                            .clip(CircleShape)
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}