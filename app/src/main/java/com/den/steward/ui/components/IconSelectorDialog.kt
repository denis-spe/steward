// Glory Be To The Name of LORD Of Hosts And Lord JESUS
package com.den.steward.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.den.steward.R

val icons = mapOf(
    "Apple" to R.drawable.apple,
    "Banana" to R.drawable.banana,
    "Achievement" to R.drawable.achievement,
    "Eggs" to R.drawable.eggs,
    "Hamburger" to R.drawable.hamburger,
    "Beef" to R.drawable.beef,
    "Coke" to R.drawable.cola,
    "Biscuits" to R.drawable.biscuits,
    "Bread" to R.drawable.bread,
    "MCdonald" to R.drawable.mcdonald_french_fries,
    "Milk" to R.drawable.milk_carton,
    "Rent" to R.drawable.rent,
    "Pizza" to R.drawable.salami_pizza,
    "Rice" to R.drawable.rice_bowl,
    "Target" to R.drawable.tag_goal,
    "Salt" to R.drawable.salt,
    "Tomato" to R.drawable.tomato,
    "Watermelon" to R.drawable.watermelon,
    "Fried chicken" to R.drawable.fried_chicken,
    "Airplane" to R.drawable.airplane,
    "Bus" to R.drawable.bus,
    "Chicken" to R.drawable.chicken,
    "Delivery" to R.drawable.delivery,
    "Fuel" to R.drawable.fuel,
    "Noodle" to R.drawable.noodles,
    "Taxi" to R.drawable.taxi,
    "Ticket" to R.drawable.tickets,
    "Television" to R.drawable.tv,
    "Flash device" to R.drawable.flash_device,
    "USB cable" to R.drawable.usb_cable,
    "Laptop" to R.drawable.laptop,
    "Phone" to R.drawable.phone,
    "Plug" to R.drawable.plug,
    "Electricity" to R.drawable.electricity,
    "Money Transfer" to R.drawable.money_transfer,
    "Payment" to R.drawable.payment_2,
    "Savings" to R.drawable.savings_2,
    "ATM" to R.drawable.atm,
    "Expense" to R.drawable.expense_2,
    "Earnings" to R.drawable.earnings_2,
    "Plan" to R.drawable.account,
    "Debt" to R.drawable.debt_2,
    "Lent" to R.drawable.pay_date,
)

data class  SelectedIcon(
    val name: String = "Apple",
    val icon: Int = R.drawable.apple
)

@Composable
fun IconSelectorDialog(
    isShown: Boolean,
    color: Color = MaterialTheme.colorScheme.primary,
    leadingIcon: @Composable () -> Unit = {},
    onDismiss: () -> Unit,
    selectedIcon: SelectedIcon = SelectedIcon(
        name = icons.keys.first(),
        icon = icons.values.first()
    ),
    onIconSelected: (SelectedIcon) -> Unit
) {
    // if not shown, return
    if (!isShown) return

    // Icon selector dialog
    val selectedIcon = remember { mutableStateOf(selectedIcon) }
    val searchState = rememberTextFieldState()

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconSelectorDialogHeader(
                    leadingIcon = leadingIcon
                )

                TextField(
                    state = searchState,
                    placeholder = {
                        Text(
                            "Search",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },

                    colors = TextFieldDefaults.colors().copy(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                HorizontalDivider(
                    modifier = Modifier.padding(top = 5.dp)
                )

                IconSelectorDialogBody(
                    defaultSelectedColor = color,
                    selectedIcon = selectedIcon.value,
                    searchState = searchState
                ) { icon ->
                    selectedIcon.value = icon
                }
                HorizontalDivider()

                IconSelectorDialogFooter(
                    color = color,
                    onDismiss = onDismiss
                ) {
                    onIconSelected(selectedIcon.value)
                }
            }
        }
    }
}

@Composable
private fun IconSelectorDialogHeader(
    leadingIcon: @Composable () -> Unit = {},
) {
    Row (
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon()

        Column (
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Select Icon",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                "Give your transaction a visual identity",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun IconSelectorDialogBody(
    defaultSelectedColor: Color = MaterialTheme.colorScheme.primary,
    selectedIcon: SelectedIcon,
    searchState: TextFieldState,
    onIconSelect: (SelectedIcon) -> Unit
) {
    val searchQuery = searchState.text.toString().trim()

    val filteredIcons = remember(searchQuery) {
        if (searchQuery.isEmpty()) {
            icons.entries.toList()
        } else {
            icons.entries.filter { (name, _) ->
                name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    if (filteredIcons.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No icons found",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .padding(vertical = 16.dp),
            contentPadding = PaddingValues(5.dp),
            userScrollEnabled = true
        ) {
            items(
                items = filteredIcons,
                key = { it.key }
            ) { (name, icon) ->
                IconContent(
                    name = name,
                    icon = icon,
                    color = defaultSelectedColor,
                    isSelected = selectedIcon.name == name
                ) {
                    onIconSelect(
                        SelectedIcon(
                            name = name,
                            icon = icon
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun IconContent(
    name: String,
    icon: Int,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    val border = if (isSelected)
        BorderStroke(1.dp, color)
    else BorderStroke(0.dp, Color.Transparent)

    OutlinedCard (
        shape = MaterialTheme.shapes.medium,
        border = border,
        modifier = Modifier
            .size(80.dp),
        onClick = onClick,
        colors = CardDefaults.outlinedCardColors()
            .copy(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = name,
                modifier = Modifier.size(27.dp)
            )

            Text(
                name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun IconSelectorDialogFooter(
    color: Color,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        TextButton(
            onClick = onDismiss
        ) {
            Text(
                "Cancel",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = color
                )
            )
        }

        VerticalDivider(
            modifier = Modifier
                .height(50.dp)
                .padding(horizontal = 5.dp)
        )

        TextButton(
            onClick = {
                onConfirm()
                onDismiss()
            },
        ) {
            Text(
                "Done",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = color
                )
            )
        }
    }
}