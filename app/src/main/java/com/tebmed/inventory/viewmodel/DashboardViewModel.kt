package com.tebmed.inventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tebmed.inventory.data.database.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardState(
    val totalProducts: Int = 0,
    val totalStock: Int = 0,
    val inventoryPurchaseValue: Long = 0,
    val inventoryWholesaleValue: Long = 0,
    val totalPurchases: Long = 0,
    val totalSales: Long = 0,
    val totalProfit: Long = 0,
    val totalExpenses: Long = 0,
    val netProfit: Long = 0,
    val customerDebts: Long = 0,
    val supplierDebts: Long = 0,
    val lowStockCount: Int = 0,
    val outOfStockCount: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val db: AppDatabase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init { observe() }

    private data class InventoryMetrics(
        val totalProducts: Int,
        val totalStock: Int,
        val inventoryPurchaseValue: Long?,
        val inventoryWholesaleValue: Long?,
        val totalPurchases: Long?
    )
    private data class FinanceMetrics(
        val totalSales: Long?,
        val expenses: Long?,
        val customerDebts: Long?,
        val supplierDebts: Long?
    )
    private data class StockMetrics(val lowStockCount: Int, val outOfStockCount: Int)

    private fun observe() {
        viewModelScope.launch {
            val inventory = combine(
                db.productDao().getTotalCount(),
                db.productDao().getTotalStock(),
                db.productDao().getTotalInventoryPurchaseValue(),
                db.productDao().getTotalInventoryWholesaleValue(),
                db.transactionDao().totalPurchases()
            ) { count, stock, purchaseValue, wholesaleValue, purchases ->
                InventoryMetrics(count, stock, purchaseValue, wholesaleValue, purchases)
            }
            val finance = combine(
                db.transactionDao().totalSales(),
                db.expenseDao().totalAmount(),
                db.customerDao().totalDebts(),
                db.supplierDao().totalDebts()
            ) { sales, expenses, customerDebt, supplierDebt ->
                FinanceMetrics(sales, expenses, customerDebt, supplierDebt)
            }
            val stockMetrics = combine(
                db.productDao().getLowStockProducts(),
                db.productDao().getOutOfStockProducts()
            ) { lowStock, outStock ->
                StockMetrics(lowStock.size, outStock.size)
            }
            combine(inventory, finance, stockMetrics) { inv, fin, stock ->
                val sales = fin.totalSales ?: 0L
                val purchases = inv.totalPurchases ?: 0L
                val profit = sales - purchases
                val expenses = fin.expenses ?: 0L
                DashboardState(
                    totalProducts = inv.totalProducts,
                    totalStock = inv.totalStock,
                    inventoryPurchaseValue = inv.inventoryPurchaseValue ?: 0L,
                    inventoryWholesaleValue = inv.inventoryWholesaleValue ?: 0L,
                    totalPurchases = purchases,
                    totalSales = sales,
                    totalProfit = profit,
                    totalExpenses = expenses,
                    netProfit = profit - expenses,
                    customerDebts = fin.customerDebts ?: 0L,
                    supplierDebts = fin.supplierDebts ?: 0L,
                    lowStockCount = stock.lowStockCount,
                    outOfStockCount = stock.outOfStockCount,
                    isLoading = false
                )
            }.collect { _state.value = it }
        }
    }

}
