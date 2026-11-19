package com.tebmed.inventory.data.dao

import androidx.room.*
import com.tebmed.inventory.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY updatedAt DESC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchProducts(query: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: Long): Product?

    @Query("SELECT * FROM products WHERE stockQuantity <= 5 ORDER BY stockQuantity ASC")
    fun getLowStockProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE stockQuantity = 0")
    fun getOutOfStockProducts(): Flow<List<Product>>

    @Query("SELECT COUNT(*) FROM products")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(stockQuantity), 0) FROM products")
    fun getTotalStock(): Flow<Int>

    @Query("SELECT SUM(stockQuantity * weightedAvgCost) FROM products")
    fun getTotalInventoryPurchaseValue(): Flow<Long?>

    @Query("SELECT SUM(stockQuantity * wholesalePrice) FROM products")
    fun getTotalInventoryWholesaleValue(): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product): Long

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE products SET stockQuantity = stockQuantity + :qty, weightedAvgCost = :newAvg, totalPurchasedQuantity = totalPurchasedQuantity + :qty, totalPurchasedAmount = totalPurchasedAmount + :amount, updatedAt = :now WHERE id = :id")
    suspend fun increaseStock(id: Long, qty: Int, amount: Long, newAvg: Long, now: Long)

    @Query("UPDATE products SET stockQuantity = stockQuantity - :qty, updatedAt = :now WHERE id = :id")
    suspend fun decreaseStock(id: Long, qty: Int, now: Long)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAll(): Flow<List<InventoryTransaction>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getByType(type: TransactionType): Flow<List<InventoryTransaction>>

    @Query("SELECT * FROM transactions WHERE productId = :productId ORDER BY date DESC")
    fun getByProduct(productId: Long): Flow<List<InventoryTransaction>>

    @Query("SELECT * FROM transactions WHERE persianDate LIKE :datePattern ORDER BY date DESC")
    fun getByPersianDate(datePattern: String): Flow<List<InventoryTransaction>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :from AND :to ORDER BY date DESC")
    fun getByDateRange(from: Long, to: Long): Flow<List<InventoryTransaction>>

    @Query("SELECT SUM(totalAmount) FROM transactions WHERE type = 'IN' AND date BETWEEN :from AND :to")
    suspend fun sumPurchases(from: Long, to: Long): Long?

    @Query("SELECT SUM(totalAmount) FROM transactions WHERE type = 'OUT' AND date BETWEEN :from AND :to")
    suspend fun sumSales(from: Long, to: Long): Long?

    @Query("SELECT SUM(totalAmount) FROM transactions WHERE type = 'IN'")
    fun totalPurchases(): Flow<Long?>

    @Query("SELECT SUM(totalAmount) FROM transactions WHERE type = 'OUT'")
    fun totalSales(): Flow<Long?>

    @Query("SELECT SUM(quantity) FROM transactions WHERE type = 'OUT' GROUP BY productId ORDER BY SUM(quantity) DESC LIMIT :limit")
    suspend fun topSellingProductIds(limit: Int): List<Long>

    @Insert
    suspend fun insert(tx: InventoryTransaction): Long

    @Delete
    suspend fun delete(tx: InventoryTransaction)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): InventoryTransaction?
}

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAll(): Flow<List<Customer>>
    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getById(id: Long): Customer?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(c: Customer): Long
    @Update
    suspend fun update(c: Customer)
    @Delete
    suspend fun delete(c: Customer)
    @Query("SELECT SUM(totalDebt) FROM customers")
    fun totalDebts(): Flow<Long?>
}

@Dao
interface SupplierDao {
    @Query("SELECT * FROM suppliers ORDER BY name ASC")
    fun getAll(): Flow<List<Supplier>>
    @Query("SELECT * FROM suppliers WHERE id = :id")
    suspend fun getById(id: Long): Supplier?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(s: Supplier): Long
    @Update
    suspend fun update(s: Supplier)
    @Delete
    suspend fun delete(s: Supplier)
    @Query("SELECT SUM(totalDebt) FROM suppliers")
    fun totalDebts(): Flow<Long?>
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAll(): Flow<List<Expense>>
    @Query("SELECT SUM(amount) FROM expenses")
    fun totalAmount(): Flow<Long?>
    @Query("SELECT SUM(amount) FROM expenses WHERE date BETWEEN :from AND :to")
    suspend fun sumBetween(from: Long, to: Long): Long?
    @Insert
    suspend fun insert(e: Expense): Long
    @Delete
    suspend fun delete(e: Expense)
    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface IncomeDao {
    @Query("SELECT * FROM incomes ORDER BY date DESC")
    fun getAll(): Flow<List<Income>>
    @Query("SELECT SUM(amount) FROM incomes")
    fun totalAmount(): Flow<Long?>
    @Insert
    suspend fun insert(i: Income): Long
    @Delete
    suspend fun delete(i: Income)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAll(): Flow<List<Category>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(c: Category): Long
    @Update
    suspend fun update(c: Category)
    @Delete
    suspend fun delete(c: Category)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecent(limit: Int = 100): Flow<List<AuditLog>>
    @Insert
    suspend fun insert(log: AuditLog)
    @Query("DELETE FROM audit_logs")
    suspend fun clearAll()
}

@Dao
interface AdminUserDao {
    @Query("SELECT * FROM admin_users WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): AdminUser?
    @Query("SELECT * FROM admin_users")
    fun getAll(): Flow<List<AdminUser>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: AdminUser): Long
    @Update
    suspend fun update(user: AdminUser)
    @Delete
    suspend fun delete(user: AdminUser)
}

@Dao
interface AppSettingDao {
    @Query("SELECT * FROM app_settings WHERE `key` = :key")
    suspend fun get(key: String): AppSetting?
    @Query("SELECT * FROM app_settings")
    fun getAll(): Flow<List<AppSetting>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun set(setting: AppSetting)
    @Query("DELETE FROM app_settings WHERE `key` = :key")
    suspend fun delete(key: String)
}
