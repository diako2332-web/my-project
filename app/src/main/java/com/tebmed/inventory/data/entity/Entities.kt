package com.tebmed.inventory.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

// ==================== Product - فقط فیلدهای درخواستی کاربر ====================
@Entity(tableName = "products", indices = [Index(value = ["name"], unique = false)])
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,                           // نام محصول
    val stockQuantity: Int = 0,                 // موجودی فعلی
    val purchasePrice: Long = 0,                // قیمت خرید به تومان
    val wholesalePrice: Long = 0,               // قیمت عمده‌فروشی به تومان
    val supplierName: String? = null,           // نام تامین‌کننده
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    // فیلدهای داخلی برای محاسبه سود دقیق (میانگین موزون)
    val weightedAvgCost: Long = 0,              // میانگین موزون بهای تمام‌شده
    val totalPurchasedQuantity: Int = 0,
    val totalPurchasedAmount: Long = 0
)

// ==================== Customer ====================
@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String? = null,
    val address: String? = null,
    val totalDebt: Long = 0,                    // بدهی به تومان
    val totalPurchases: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)

// ==================== Supplier ====================
@Entity(tableName = "suppliers")
data class Supplier(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String? = null,
    val address: String? = null,
    val totalDebt: Long = 0,                    // بدهی ما به تامین‌کننده
    val totalPurchases: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)

// ==================== Inventory Transaction ====================
@Entity(tableName = "transactions", indices = [Index("productId"), Index("date")])
data class InventoryTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val productName: String,
    val type: TransactionType,                  // IN / OUT
    val quantity: Int,
    val unitPrice: Long,                        // قیمت واحد به تومان
    val totalAmount: Long,
    val supplierName: String? = null,
    val customerName: String? = null,
    val invoiceNumber: String? = null,
    val date: Long = System.currentTimeMillis(), // تاریخ شمسی به میلی‌ثانیه
    val persianDate: String = "",               // تاریخ شمسی رشته‌ای 1403/08/28
    val time: String = "",                      // ساعت 14:30
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val description: String? = null,
    val isPaid: Boolean = true,
    val paidAmount: Long = totalAmount,
    val remainingDebt: Long = 0,
    val requiresAdminOverride: Boolean = false, // اگر موجودی منفی شده
    val createdAt: Long = System.currentTimeMillis()
)

enum class TransactionType { IN, OUT, ADJUSTMENT, RETURN }
enum class PaymentMethod { CASH, CARD, BANK_TRANSFER, COMBINED, CREDIT }

// ==================== Expense ====================
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Long,
    val category: String? = null,
    val date: Long = System.currentTimeMillis(),
    val persianDate: String = "",
    val description: String? = null,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH
)

// ==================== Income (درآمد متفرقه) ====================
@Entity(tableName = "incomes")
data class Income(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Long,
    val category: String? = null,
    val date: Long = System.currentTimeMillis(),
    val persianDate: String = "",
    val description: String? = null
)

// ==================== Category (دسته‌بندی محصولات) ====================
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String? = null,
    val color: String? = null,
    val productCount: Int = 0
)

// ==================== Audit Log ====================
@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String,
    val details: String? = null,
    val userName: String = "admin",
    val timestamp: Long = System.currentTimeMillis(),
    val persianDateTime: String = ""
)

// ==================== Admin User ====================
@Entity(tableName = "admin_users")
data class AdminUser(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val passwordHash: String,                   // BCrypt hashed
    val displayName: String? = null,
    val isActive: Boolean = true,
    val role: String = "ADMIN",                 // ADMIN, STAFF
    val createdAt: Long = System.currentTimeMillis()
)

// ==================== App Settings ====================
@Entity(tableName = "app_settings")
data class AppSetting(
    @PrimaryKey val key: String,
    val value: String
)
