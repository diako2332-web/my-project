package com.tebmed.inventory.utils

import android.content.Context
import android.os.Environment
import com.tebmed.inventory.data.entity.InventoryTransaction
import java.io.File
import java.io.FileWriter

object ExportUtils {

    fun exportToCsv(context: Context, transactions: List<InventoryTransaction>, fileName: String): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "TebMedExports")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "$fileName.csv")
        FileWriter(file).use { w ->
            w.append("شناسه,محصول,نوع,تعداد,قیمت واحد,مبلغ کل,تامین‌کننده,مشتری,شماره فاکتور,تاریخ,ساعت,روش پرداخت\n")
            transactions.forEach { t ->
                w.append("${t.id},${t.productName},${t.type},${t.quantity},${t.unitPrice},${t.totalAmount},${t.supplierName?:"-"},${t.customerName?:"-"},${t.invoiceNumber?:"-"},${t.persianDate},${t.time},${t.paymentMethod}\n")
            }
        }
        return file
    }

    // Excel via Apache POI (simplified CSV with xlsx extension fallback if POI heavy)
    // For full xlsx, use Workbook

    // PDF export would use iText - placeholder
    fun getExportDir(context: Context): File {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "TebMedExports")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }
}
