// Bless be to the LORD GOD of hosts
package com.den.steward.helper

import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.text.TextRange
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.backend.dataStructure.Transaction
import net.objecthunter.exp4j.ExpressionBuilder
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Locale
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow

val String.title: String
    get() {
        return this.replaceFirstChar { it.uppercase() }
    }

val String.isEmailValid: String?
    get() {
        val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")

        return if (this.isBlank() || this.isEmpty()) {
            "Email cannot be blank"
        } else if (!emailRegex.matches(this)) {
            "Invalid email format"
        } else {
            null
        }
    }

val String.isPasswordValid: String?
    get() {
        // Example password validation (at least 6 characters)
        return if (this.isBlank() && this.isEmpty()) {
            "Password cannot be empty"
        } else if (this.length < 8) {
            "Password must be at least 8 characters long"
        } else {
            null
        }
    }

val String.isNameValid: String?
    get() {
        return if (this.isBlank() || this.isEmpty()) {
            "Name cannot be blank"
        } else {
            null
        }
    }

fun NavBackStack<NavKey>.pop() {
    if (this.size > 1) {
        this.removeLastOrNull()
    }
}

fun specificDateTime(year: Int, month: Int, day: Int, hour: Int, minute: Int): LocalDateTime {
    return LocalDateTime.of(year, month, day, hour, minute)
}

fun LocalDateTime.yesterday(time: java.time.LocalTime? = null): LocalDateTime {
    val yesterday = this.minusDays(1)
    return if (time != null) {
        yesterday.with(time).withSecond(0).withNano(0)
    } else {
        yesterday
    }
}

fun LocalDateTime.toEpochMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long {
    return this.atZone(zoneId).toInstant().toEpochMilli()
}

// Extension to convert Long to LocalDateTime
fun Long.toLocalDateTime(zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
    return Instant.ofEpochMilli(this).atZone(zoneId).toLocalDateTime()
}

fun Double.formatValueOnly(): String {
    val absValue = abs(this)
    if (absValue < 1_000_000) {
        val rounding = BigDecimal(this).setScale(2, RoundingMode.HALF_UP)
        return rounding.abs().toString()
            .replace(Regex("\\B(?=(\\d{3})+(?!\\d))"), ",")
            .replace(Regex("\\.00$"), "")
    }

    val suffixes = charArrayOf('M', 'B', 'T', 'Q')
    val formatter = DecimalFormat("#.##")
    val base = (log10(absValue) / 3).toInt()
    val scaledNumber = absValue / 1000.0.pow(base.toDouble())
    val suffixIndex = base - 2

    return if (suffixIndex >= 0 && suffixIndex < suffixes.size) {
        "${formatter.format(scaledNumber)}${suffixes[suffixIndex]}"
    } else {
        String.format(Locale.US, "%.2f", absValue)
    }
}

/**
 * Calculates the Mean: The average of all values in a list.
 */
fun <T> List<T>.mean(selector: (T) -> Double): Double {
    if (isEmpty()) return 0.0
    return sumOf { selector(it) } / size
}


/**
 * Calculates the Standard Deviation: The square root of the Variance.
 */
val List<Double>.std: Double
    get() = kotlin.math.sqrt(variance)

/**
 * Calculates the Variance: The average of the squared differences from the Mean.
 */
val List<Double>.variance: Double
    get() {
        if (isEmpty()) return 0.0
        val avg = average()
        return sumOf { (it - avg) * (it - avg) } / size
    }


/**
 * Calculates the Mode: The most frequent value in a list.
 */
fun List<Double>.mode(): List<Double> {
    if (isEmpty()) return emptyList()
    val counts = groupBy { it }.mapValues { it.value.size }
    val maxCount = counts.maxOf { it.value }
    return counts.filter { it.value == maxCount }.keys.toList()
}

/**
 * Calculates Quartiles: Q1, Q2 (Median), Q3.
 */
fun List<Double>.quartiles(): Triple<Double, Double, Double> {
    if (size < 4) return Triple(0.0, 0.0, 0.0)
    val sorted = sorted()

    fun getMedian(data: List<Double>): Double {
        if (data.isEmpty()) return 0.0
        val mid = data.size / 2
        return if (data.size % 2 == 0) (data[mid - 1] + data[mid]) / 2.0 else data[mid]
    }

    val q2 = getMedian(sorted)
    val lowerHalf = sorted.take(size / 2)
    val upperHalf = if (size % 2 == 0) sorted.takeLast(size / 2) else sorted.takeLast(size / 2)

    val q1 = getMedian(lowerHalf)
    val q3 = getMedian(upperHalf)

    return Triple(q1, q2, q3)
}

/**
 * Calculates the Interquartile Range (IQR).
 */
fun List<Double>.iqr(): Double {
    val (q1, _, q3) = quartiles()
    return q3 - q1
}

/**
 * Calculates the Skewness: Measure of asymmetry.
 */
fun List<Double>.skewness(): Double {
    if (size < 3) return 0.0
    val avg = average()
    val stdDev = kotlin.math.sqrt(map { (it - avg) * (it - avg) }.sum() / size)
    if (stdDev == 0.0) return 0.0

    val m3 = sumOf { (it - avg).pow(3.0) } / size
    return m3 / stdDev.pow(3.0)
}

/**
 * Calculates the Kurtosis: Measure of "tailedness".
 */
fun List<Double>.kurtosis(): Double {
    if (size < 4) return 0.0
    val avg = average()
    val variance = sumOf { (it - avg) * (it - avg) } / size
    if (variance == 0.0) return 0.0

    val m4 = sumOf { (it - avg).pow(4.0) } / size
    return (m4 / variance.pow(2.0)) - 3.0
}

/**
 * Calculates the Z-score for a given value relative to a list of values.
 */
fun Double.zScore(values: List<Double>): Double {
    if (values.isEmpty()) return 0.0
    val avg = values.average()
    val variance = values.sumOf { (it - avg) * (it - avg) } / values.size
    val stdDev = kotlin.math.sqrt(variance)
    if (stdDev == 0.0) return 0.0
    return (this - avg) / stdDev
}

fun Long.formatToAmount(): String {
    return this.toDouble().formatToAmount().split(".")[0]
}


fun Float.formatToAmount(): String {
    return this.toDouble().formatToAmount()
}

fun Int.formatToAmount(): String {
    return this.toDouble().formatToAmount().split(".")[0]
}

fun TextFieldState.setTextAndPlaceCursorAtEnd(text: String) {
    edit {
        replace(0, length, text)
        selection = TextRange(length)
    }
}


infix fun Int.formatToTime(minutes: Int): String = String.format(
    Locale.getDefault(),
    "%02d:%02d",
    this, minutes
)

fun String.limitLength(maxLength: Int): String {
    return if (this.length > maxLength) {
        this.substring(0, maxLength) + "..."
    } else {
        this
    }
}


// Cache the formatter so it isn't recreated on every invocation
private val cachedDecimalFormat = DecimalFormat("#.##")
private val suffixes = charArrayOf('M', 'B', 'T', 'Q')

fun Double.formatToAmount(): String {
    val absValue = abs(this)
    val sign = if (this < 0) "-" else ""

    // Resolve system currency symbol without full heavy instance creation where possible
    val symbol = try {
        NumberFormat.getCurrencyInstance(Locale.getDefault()).currency?.symbol ?: "$"
    } catch (e: Exception) {
        "$"
    }

    if (absValue < 1_000_000) {
        val rounding = BigDecimal(this).setScale(2, RoundingMode.HALF_UP)
        // Use Java/Kotlin built-in group formatting instead of heavy Regex replaces
        val formattedAmount = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
            maximumFractionDigits = 2
            minimumFractionDigits = 0
        }.format(rounding.abs())

        return "$sign$symbol $formattedAmount"
    }

    val base = (log10(absValue) / 3).toInt()
    val scaledNumber = absValue / 1000.0.pow(base.toDouble())
    val suffixIndex = base - 2

    return if (suffixIndex in suffixes.indices) {
        "$sign$symbol ${cachedDecimalFormat.format(scaledNumber)}${suffixes[suffixIndex]}"
    } else {
        "$sign$symbol ${String.format(Locale.US, "%.2f", absValue)}"
    }
}

val Int.addZeroIfLessThenTen: String
    get() = if (this < 10) "0$this" else this.toString()

val Double.formatResult: String
    get() {
        return if (this % 1.0 == 0.0) {
            toInt().toString()
        } else {
            String.format(Locale.US, "%.4f", this)
                .trimEnd('0')
                .trimEnd('.')
        }
    }
private val evalRegex = Regex("(\\s)")
val CharSequence.eval: Double
    get() {
        val expression = replace(evalRegex, "")
            .replace('÷', '/')
            .replace('×', '*')
            .replace(",", "")
        Log.d("StringEval", expression)
        return try {
            ExpressionBuilder(expression).build().evaluate()
        } catch (_: Exception) {
            0.0
        }
    }

val LocalDateTime.formatedDateTime: String
    get() {
        val day = this.toLocalDate()
            .dayOfMonth
            .addZeroIfLessThenTen
        val month = this.month.name.take(3).title
        val year = this.year
        val hour = this.hour.addZeroIfLessThenTen
        val minute = this.minute.addZeroIfLessThenTen
        return "$day ${month.title} $year, $hour:$minute"
    }

val LocalDate.formattedDate: String
    get() {
        val day = this
            .dayOfMonth
            .addZeroIfLessThenTen
        val month = this.month.name.take(3).title
        val year = this.year
        return "$day $month $year"
    }

val LocalDateTime.formattedDate: String
    get() {
        val day =  this
            .dayOfMonth
            .addZeroIfLessThenTen
        val month = this.month.name.take(3).title
        val year = this.year
        return "$day $month $year"
    }

val LocalTime.formattedTime: String
    get() {
        val hour = this.hour.addZeroIfLessThenTen
        val minute = this.minute.addZeroIfLessThenTen
        return "$hour:$minute"
    }

val LocalDateTime.formattedTime: String
    get() {
        val hour = this.hour.addZeroIfLessThenTen
        val minute = this.minute.addZeroIfLessThenTen
        return "$hour:$minute"
    }

val LocalDate.toEpochMillis: Long
    get() {
        return this.atStartOfDay().toEpochMillis(ZoneId.systemDefault())
    }

infix fun LocalDate.combine(time: LocalTime): Long {
    return this.atTime(time).toEpochMillis(ZoneId.systemDefault())
}