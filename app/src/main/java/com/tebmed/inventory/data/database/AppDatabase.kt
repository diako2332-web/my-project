package com.tebmed.inventory.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tebmed.inventory.data.dao.*
import com.tebmed.inventory.data.entity.*
import com.tebmed.inventory.utils.Converters

@Database(
    entities = [
        Product::class,
        InventoryTransaction::class,
        Customer::class,
        Supplier::class,
        Expense::class,
        Income::class,
        Category::class,
        AuditLog::class,
        AdminUser::class,
        AppSetting::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun transactionDao(): TransactionDao
    abstract fun customerDao(): CustomerDao
    abstract fun supplierDao(): SupplierDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun incomeDao(): IncomeDao
    abstract fun categoryDao(): CategoryDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun adminUserDao(): AdminUserDao
    abstract fun appSettingDao(): AppSettingDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tebmed_inventory.db"
                )
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
