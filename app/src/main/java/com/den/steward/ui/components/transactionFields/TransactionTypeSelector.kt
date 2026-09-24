// Grace and truth came through JESUS our LORD
package com.den.steward.ui.components.transactionFields

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.den.steward.backend.entitles.TransactionType
import com.den.steward.helper.title


@Composable
fun TransactionTypeSelector(
    modifier: Modifier = Modifier,
    selectedType: TransactionType,
    onTypeChange: (TransactionType) -> Unit
) {
    val types = remember {
        TransactionType.entries.filter {
            it !in listOf(
                TransactionType.GOAL,
                TransactionType.ATTAIN,
                TransactionType.PLAN_FULFILLMENT,
                TransactionType.PLAN,
                TransactionType.ACHIEVEMENT
            )
        }
    }

    LazyRow(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        items(types.size, key = {
            types[it].ordinal
        }) { index ->
            val type = types[index]
            TransactionTypeButton(
                type = type,
                isSelected = type == selectedType,
                onClick = { onTypeChange(type) }
            )
        }
    }
}

@Composable
fun TransactionTypeButton(type: TransactionType, isSelected: Boolean, onClick: () -> Unit) {
    val color = colorResource(type.color)

    AssistChip(
        label = {
            Text(
                text = type.name.title,
            )
        },
        onClick = onClick,

        border = if (isSelected) BorderStroke(1.dp, color) else null,

        leadingIcon = {
            Image(
                painter = painterResource(type.icon),
                contentDescription = "Localized description",
                Modifier.size(AssistChipDefaults.IconSize),
                colorFilter = ColorFilter.tint(color)
            )
        }
    )
}