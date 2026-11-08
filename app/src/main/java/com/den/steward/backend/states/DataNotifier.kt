// Glory be to the name of the LORD GOD
package com.den.steward.backend.states

import com.den.steward.R

data class DataNotifier(
    val text: String = "",
    val icon: Int = R.drawable.description,
    val color: Int = R.color.steward_icon,
    val amount: String = ""
)
