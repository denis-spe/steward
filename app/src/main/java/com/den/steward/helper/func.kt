// Glory be to name of LORD GOD
package com.den.steward.helper

import com.den.steward.backend.entitles.LiabilitiesStatus
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.Affected
import com.den.steward.backend.states.Filter
import com.den.steward.backend.states.Filter.Companion.toTransactionType
import com.den.steward.backend.states.OrderBy
import com.den.steward.backend.states.SortBy
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.text.NumberFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

private val cachedCurrencySymbol: String by lazy {
    try {
        NumberFormat.getCurrencyInstance(Locale.getDefault()).currency?.symbol ?: "$"
    } catch (e: Exception) {
        "$"
    }
}

fun getCurrencySymbol(): String {
    return cachedCurrencySymbol
}

fun getStartOfDayMillis(dayOffset: Long = 0): Long {
    val zone = ZoneId.systemDefault()
    return LocalDate.now(zone)
        .plusDays(dayOffset)
        .atStartOfDay(zone)
        .toInstant()
        .toEpochMilli()
}

fun filterAndSortTodayTransactions(
    transactions: List<Transaction>,
    startOfToday: Long,
    startOfTomorrow: Long,
    filter: List<Filter>,
    orderBy: OrderBy,
    sortBy: SortBy
): ImmutableList<Transaction> {
    val targetTypes = filter.mapNotNull { it.toTransactionType }

    val comparator = when (sortBy) {
        SortBy.TIME -> compareBy<Transaction> { it.createdAt }
        SortBy.AMOUNT -> compareBy { it.getAmountOrValue ?: 0.0 }
        SortBy.LABEL -> compareBy { it.getLabel.lowercase() }
        SortBy.FULFILLED -> compareBy { transaction ->
            when (transaction) {
                is Transaction.Lent -> transaction.remainingAmount
                is Transaction.Debt -> transaction.remainingAmount
                is Transaction.Goal -> transaction.remainingValue
                else -> 0.0
            }
        }
    }

    val finalComparator = if (orderBy == OrderBy.ASCENDING) comparator else comparator.reversed()
    val minTime = minOf(startOfToday, startOfTomorrow)
    val maxTime = maxOf(startOfToday, startOfTomorrow)

    return transactions
        .asSequence()
        .filter { it.createdAt in minTime until maxTime }
        .filter { transaction ->
            filter.all {
                when (it) {
                    Filter.UNPAID -> {
                        when (transaction) {
                            is Transaction.Lent -> transaction.status == LiabilitiesStatus.UNPAID
                            is Transaction.Debt -> transaction.status == LiabilitiesStatus.UNPAID
                            else -> false
                        }
                    }
                    Filter.PAID -> {
                        when (transaction) {
                            is Transaction.Lent -> transaction.status == LiabilitiesStatus.PAID
                            is Transaction.Debt -> transaction.status == LiabilitiesStatus.PAID
                            else -> false
                        }
                    }
                    Filter.AFFECTED -> {
                        transaction.getAffectedAmount == Affected.AFFECTED
                    }
                    Filter.UN_AFFECTED -> {
                        transaction.getAffectedAmount == Affected.NEUTRAL
                    }
                    else -> targetTypes.isEmpty() || transaction.type in targetTypes
                }
            }
        }
        .sortedWith(finalComparator)
        .toImmutableList()
}