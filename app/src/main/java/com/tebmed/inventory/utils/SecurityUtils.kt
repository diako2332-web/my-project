package com.tebmed.inventory.utils

import org.mindrot.jbcrypt.BCrypt

object SecurityUtils {
    fun hashPassword(plain: String): String = BCrypt.hashpw(plain, BCrypt.gensalt(12))
    fun verifyPassword(plain: String, hash: String): Boolean = try { BCrypt.checkpw(plain, hash) } catch (e: Exception) { false }

    // تولید شماره فاکتور خودکار: INV-14030828-XXXX
    fun generateInvoiceNumber(): String {
        val date = PersianUtils.nowPersianDate().replace("/", "")
        val rnd = (1000..9999).random()
        return "INV-$date-$rnd"
    }
    fun generatePurchaseInvoice(): String {
        val date = PersianUtils.nowPersianDate().replace("/", "")
        val rnd = (1000..9999).random()
        return "PUR-$date-$rnd"
    }
}
