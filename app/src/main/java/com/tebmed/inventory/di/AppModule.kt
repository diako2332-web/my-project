package com.tebmed.inventory.di

import android.content.Context
import com.tebmed.inventory.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase = AppDatabase.getInstance(ctx)

    @Provides fun provideProductDao(db: AppDatabase) = db.productDao()
    @Provides fun provideTransactionDao(db: AppDatabase) = db.transactionDao()
    @Provides fun provideCustomerDao(db: AppDatabase) = db.customerDao()
    @Provides fun provideSupplierDao(db: AppDatabase) = db.supplierDao()
    @Provides fun provideExpenseDao(db: AppDatabase) = db.expenseDao()
    @Provides fun provideIncomeDao(db: AppDatabase) = db.incomeDao()
    @Provides fun provideCategoryDao(db: AppDatabase) = db.categoryDao()
    @Provides fun provideAuditDao(db: AppDatabase) = db.auditLogDao()
    @Provides fun provideAdminDao(db: AppDatabase) = db.adminUserDao()
    @Provides fun provideSettingDao(db: AppDatabase) = db.appSettingDao()
}
