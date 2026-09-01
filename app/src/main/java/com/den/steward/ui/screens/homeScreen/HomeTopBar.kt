// Grace and truth came through JESUS CHRIST
package com.den.steward.ui.screens.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.den.steward.backend.states.HomeTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(currentTab: HomeTab, onTabChange: (HomeTab) -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            HomeTabRow(
                currentTab = currentTab,
                onTabChange = onTabChange
            )
        }
    )
}

@Composable
fun HomeTabRow(currentTab: HomeTab, onTabChange: (HomeTab) -> Unit) {
    val tabs = remember {
        HomeTab.entries.toTypedArray()
    }
    Surface (
        shadowElevation = 3.dp,
        tonalElevation = 1.dp,
        shape = CircleShape,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.7f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PrimaryScrollableTabRow(
                currentTab.idx,
                divider = {},
                indicator = {}
            ) {
                tabs.forEach { tab ->
                    val isSelected = currentTab == tab

                    val color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }

                    val backgroundColor = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Transparent
                    }

                    Tab(
                        selected = currentTab == tab,
                        onClick = {
                            onTabChange(tab)
                        },
                        text = {
                            Text(
                                tab.label,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = color
                            )
                        },
                        selectedContentColor = color,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(backgroundColor)
                    )

                }
            }
        }
    }
}