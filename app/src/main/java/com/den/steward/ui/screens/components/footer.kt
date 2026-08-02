// Love the LORD your GOD with all your soul and with all your heart
// and with all your might and love your neighbor as yourself
package com.den.steward.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun Footer() {
    Column (
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier.height(40.dp)
        )
        Text(
            "Glory be the name of LORD GOD",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            "By continuing, you agree to our",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            "Terms of Service and Privacy Policy",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            "Copy right©2023, All rights reserved",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

