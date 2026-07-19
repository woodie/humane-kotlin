package com.netpress.humane

import java.time.Instant
import java.util.Locale
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.roundToLong

/**
 * Human-readable file sizes and relative times, matching Finder/ActionView
 * wording -- same algorithm and fixtures as humane (Go)/humane-ruby/
 * humane-swift. See docs/COMMENTS.md.
 */
object Humane {
    private val sizeUnits = listOf("KB", "MB", "GB", "TB", "PB", "EB")

    /** Humane.humanSize(225_935) == "226 KB" */
    fun humanSize(bytes: Long): String {
        if (bytes == 0L) return "Zero KB"
        if (bytes < 1000) return if (bytes == 1L) "1 byte" else "$bytes bytes"

        var exp = floor(ln(bytes.toDouble()) / ln(1000.0)).toInt()
        if (exp > sizeUnits.size) exp = sizeUnits.size
        val value = bytes.toDouble() / Math.pow(1000.0, exp.toDouble())

        return "${formatSignificant(value, 3)} ${sizeUnits[exp - 1]}"
    }

    /** Humane.distanceInTime(at, relativeTo) == "3 minutes ago" */
    fun distanceInTime(
        at: Instant?,
        relativeTo: Instant,
        approximate: Boolean = true,
        includeSeconds: Boolean = false,
        whenNil: String = "",
    ): String {
        if (at == null) return whenNil

        var seconds = relativeTo.epochSecond - at.epochSecond
        val future = seconds < 0
        seconds = abs(seconds)

        if (!includeSeconds && seconds < 30) {
            return if (future) "in less than a minute" else "less than a minute ago"
        }
        if (includeSeconds && seconds < 60) {
            return wrap(pluralize(seconds, "second"), future)
        }

        // Buckets come from distanceInMinutes, not raw seconds re-divided per unit -- see docs/COMMENTS.md.
        val distanceInMinutes = (seconds / 60.0).roundToLong()
        val (text, approximable) = when {
            distanceInMinutes == 1L -> "1 minute" to false
            distanceInMinutes in 2L..44L -> pluralize(distanceInMinutes, "minute") to false
            distanceInMinutes in 45L..89L -> "1 hour" to true
            distanceInMinutes in 90L..1439L -> pluralize((distanceInMinutes / 60.0).roundToLong(), "hour") to true
            distanceInMinutes in 1440L..2519L -> "1 day" to false
            else -> pluralize((distanceInMinutes / 1440.0).roundToLong(), "day") to false
        }

        val finalText = if (approximate && approximable) "about $text" else text
        return wrap(finalText, future)
    }

    /**
     * Humane.timeAgo(at) == "3 minutes ago" -- at is non-nullable by design
     * (matching humane's Go v0.9.4 change): every real caller has a concrete
     * timestamp, so this convenience doesn't need the nil-handling
     * distanceInTime keeps for its own explicit core use. whenNil is kept
     * only for signature parity with distanceInTime's options -- it can
     * never actually trigger here.
     */
    fun timeAgo(
        at: Instant,
        approximate: Boolean = true,
        includeSeconds: Boolean = false,
        whenNil: String = "",
    ): String = distanceInTime(at, Instant.now(), approximate, includeSeconds, whenNil)

    private fun wrap(text: String, future: Boolean) = if (future) "in $text" else "$text ago"

    private fun pluralize(count: Long, unit: String) = if (count == 1L) "1 $unit" else "$count ${unit}s"

    private fun formatSignificant(value: Double, sigFigs: Int): String {
        val magnitude = floor(log10(value)).toInt() + 1
        val decimals = (sigFigs - magnitude).coerceAtLeast(0)
        var s = String.format(Locale.ROOT, "%.${decimals}f", value)
        if (s.contains(".")) {
            s = s.trimEnd('0').trimEnd('.')
        }
        return s
    }
}
