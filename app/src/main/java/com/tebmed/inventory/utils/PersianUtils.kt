package com.tebmed.inventory.utils

import java.text.NumberFormat
import java.util.Locale
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat

object PersianUtils {

    // تبدیل میلی‌ثانیه به تاریخ شمسی yyyy/MM/dd
    fun toPersianDate(millis: Long): String {
        val pd = PersianDate(millis)
        return PersianDateFormat.format(pd, "Y/m/d")
    }

    fun toPersianDateTime(millis: Long): String {
        val pd = PersianDate(millis)
        return PersianDateFormat.format(pd, "Y/m/d H:i")
    }

    fun nowPersianDate(): String = toPersianDate(System.currentTimeMillis())

    fun nowPersianTime(): String {
        val c = java.util.Calendar.getInstance()
        return String.format("%02d:%02d", c.get(java.util.Calendar.HOUR_OF_DAY), c.get(java.util.Calendar.MINUTE))
    }

    // اعداد فارسی
    private val persianDigits = mapOf('0' to '۰','1' to '۱','2' to '۲','3' to '۳','4' to '۴','5' to '۵','6' to '۶','7' to '۷','8' to '۸','9' to '۹')
    fun toPersianDigits(input: String): String = input.map { persianDigits[it] ?: it }.joinToString("")
    fun toPersianDigits(input: Long): String = toPersianDigits(input.toString())

    // فرمت تومان با جداکننده هزارگان
    fun formatToman(amount: Long, usePersianDigits: Boolean = false): String {
        val nf = NumberFormat.getNumberInstance(Locale("fa"))
        val formatted = nf.format(amount)
        val withDigits = if (usePersianDigits) toPersianDigits(formatted) else formatted
        return "$withDigits تومان"
    }

    fun formatNumber(amount: Long, usePersianDigits: Boolean = false): String {
        val nf = NumberFormat.getNumberInstance(Locale.US)
        val f = nf.format(amount)
        return if (usePersianDigits) toPersianDigits(f) else f
    }

    fun formatNumberInt(amount: Int, usePersianDigits: Boolean = false): String {
        return formatNumber(amount.toLong(), usePersianDigits)
    }

    // محاسبه بازه ماه شمسی فعلی
    fun currentPersianMonthRange(): Pair<Long, Long> {
        val now = PersianDate()
        val startPd = PersianDate().apply {
            setShYear(now.shYear)
            setShMonth(now.shMonth)
            setShDay(1)
            setHour(0); setMinute(0); setSecond(0)
        }
        val endPd = PersianDate().apply {
            setShYear(now.shYear)
            setShMonth(now.shMonth)
            setShDay(now.monthDays)
            setHour(23); setMinute(59); setSecond(59)
        }
        return startPd.time to endPd.time
    }
}
