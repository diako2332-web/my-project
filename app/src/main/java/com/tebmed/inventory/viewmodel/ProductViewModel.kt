package com.tebmed.inventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.tebmed.inventory.data.database.AppDatabase
import com.tebmed.inventory.data.entity.Product
import com.tebmed.inventory.utils.PersianUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(private val db: AppDatabase) : ViewModel() {
    val products = db.productDao().getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val filteredProducts = combine(products, _searchQuery) { list, q ->
        if (q.isBlank()) list else list.filter { it.name.contains(q, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearch(q: String) { _searchQuery.value = q }

    suspend fun addProduct(name: String, stock: Int, purchasePrice: Long, wholesalePrice: Long, supplier: String?): Result<Long> {
        if (name.isBlank()) return Result.failure(Exception("نام محصول الزامی است"))
        if (stock < 0) return Result.failure(Exception("موجودی اولیه نمی‌تواند منفی باشد"))
        if (purchasePrice < 0 || wholesalePrice < 0) return Result.failure(Exception("قیمت نباید منفی باشد"))
        if (purchasePrice > 0 && stock.toLong() > Long.MAX_VALUE / purchasePrice) return Result.failure(Exception("مبلغ موجودی اولیه بیش از حد مجاز است"))
        val p = Product(
            name = name.trim(),
            stockQuantity = stock,
            purchasePrice = purchasePrice,
            wholesalePrice = wholesalePrice,
            supplierName = supplier?.takeIf { it.isNotBlank() },
            weightedAvgCost = purchasePrice,
            totalPurchasedQuantity = stock,
            totalPurchasedAmount = purchasePrice * stock
        )
        val id = db.productDao().insert(p)
        db.auditLogDao().insert(com.tebmed.inventory.data.entity.AuditLog(action = "افزودن محصول", details = name, persianDateTime = PersianUtils.toPersianDateTime(System.currentTimeMillis())))
        return Result.success(id)
    }

    suspend fun updateProduct(p: Product) {
        db.productDao().update(p.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteProduct(id: Long) {
        db.productDao().deleteById(id)
    }

    // ورود کالا - محاسبه میانگین موزون
    suspend fun stockIn(productId: Long, qty: Int, unitPrice: Long, supplierName: String?, invoice: String?, paymentMethod: com.tebmed.inventory.data.entity.PaymentMethod, desc: String?): Result<Unit> {
        if (qty <= 0) return Result.failure(Exception("تعداد ورود باید بیشتر از صفر باشد"))
        if (unitPrice < 0) return Result.failure(Exception("قیمت واحد نمی‌تواند منفی باشد"))
        if (unitPrice > 0 && qty.toLong() > Long.MAX_VALUE / unitPrice) return Result.failure(Exception("مبلغ تراکنش بیش از حد مجاز است"))
        val product = db.productDao().getById(productId) ?: return Result.failure(Exception("محصول یافت نشد"))
        if (product.stockQuantity > Int.MAX_VALUE - qty) return Result.failure(Exception("موجودی از حد مجاز بیشتر می‌شود"))
        val totalAmount = qty.toLong() * unitPrice
        val oldQty = product.totalPurchasedQuantity
        if (oldQty > Int.MAX_VALUE - qty) return Result.failure(Exception("تعداد خریدهای ثبت‌شده از حد مجاز بیشتر می‌شود"))
        val oldAmount = product.totalPurchasedAmount
        if (oldAmount > Long.MAX_VALUE - totalAmount) return Result.failure(Exception("مجموع مبلغ خرید از حد مجاز بیشتر می‌شود"))
        val newTotalQty = oldQty + qty
        val newTotalAmount = oldAmount + totalAmount
        val newAvg = if (newTotalQty > 0) newTotalAmount / newTotalQty else unitPrice
        val now = System.currentTimeMillis()
        val tx = com.tebmed.inventory.data.entity.InventoryTransaction(
            productId = productId,
            productName = product.name,
            type = com.tebmed.inventory.data.entity.TransactionType.IN,
            quantity = qty,
            unitPrice = unitPrice,
            totalAmount = totalAmount,
            supplierName = supplierName,
            invoiceNumber = invoice ?: com.tebmed.inventory.utils.SecurityUtils.generatePurchaseInvoice(),
            persianDate = PersianUtils.nowPersianDate(),
            time = PersianUtils.nowPersianTime(),
            paymentMethod = paymentMethod,
            description = desc
        )
        return try {
            db.withTransaction {
                db.productDao().increaseStock(productId, qty, totalAmount, newAvg, now)
                db.transactionDao().insert(tx)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // خروج کالا با چک موجودی
    suspend fun stockOut(productId: Long, qty: Int, unitPrice: Long, customerName: String?, invoice: String?, paymentMethod: com.tebmed.inventory.data.entity.PaymentMethod, desc: String?, adminOverride: Boolean = false): Result<Unit> {
        if (qty <= 0) return Result.failure(Exception("تعداد خروج باید بیشتر از صفر باشد"))
        if (unitPrice < 0) return Result.failure(Exception("قیمت فروش نمی‌تواند منفی باشد"))
        if (unitPrice > 0 && qty.toLong() > Long.MAX_VALUE / unitPrice) return Result.failure(Exception("مبلغ تراکنش بیش از حد مجاز است"))
        val product = db.productDao().getById(productId) ?: return Result.failure(Exception("محصول یافت نشد"))
        if (!adminOverride && qty > product.stockQuantity) {
            return Result.failure(Exception("موجودی کافی نیست! موجودی فعلی: ${product.stockQuantity} - نیاز به تایید ادمین"))
        }
        val totalAmount = qty.toLong() * unitPrice
        val tx = com.tebmed.inventory.data.entity.InventoryTransaction(
            productId = productId,
            productName = product.name,
            type = com.tebmed.inventory.data.entity.TransactionType.OUT,
            quantity = qty,
            unitPrice = unitPrice,
            totalAmount = totalAmount,
            customerName = customerName,
            invoiceNumber = invoice ?: com.tebmed.inventory.utils.SecurityUtils.generateInvoiceNumber(),
            persianDate = PersianUtils.nowPersianDate(),
            time = PersianUtils.nowPersianTime(),
            paymentMethod = paymentMethod,
            description = desc,
            requiresAdminOverride = qty > product.stockQuantity
        )
        return try {
            db.withTransaction {
                db.productDao().decreaseStock(productId, qty, System.currentTimeMillis())
                db.transactionDao().insert(tx)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
