// Bless be the name of LORD GOD
package com.den.steward.ui.components.transactionFields

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.den.steward.R

@Composable
fun TransactionFieldCard(
    title: String,
    modifier: Modifier = Modifier,
    colors: ListItemColors = ListItemDefaults.colors(),
    headlineContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier)
                .height(LIST_ITEM_HEIGHT)
                .clickable {
                    onClick?.invoke()
                },
            leadingContent = leadingContent,
            colors = colors,
            headlineContent = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(title, fontSize = FONT_SIZE, fontWeight = FONT_WEIGHT)
                    headlineContent?.invoke()
                }
            },

            trailingContent = trailingContent
        )

        HorizontalDivider(
            modifier = Modifier.padding(bottom = 2.dp),
            color = MaterialTheme.colorScheme.background
        )
    }
}