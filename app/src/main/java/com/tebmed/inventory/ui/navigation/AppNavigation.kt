package com.tebmed.inventory.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tebmed.inventory.ui.screens.*

sealed class Screen(val route: String, val title: String) {
    object Dashboard : Screen("dashboard", "داشبورد اصلی")
    object Products : Screen("products", "مدیریت محصولات")
    object Categories : Screen("categories", "دسته‌بندی محصولات")
    object StockIn : Screen("stock_in", "ورود کالا به انبار")
    object StockOut : Screen("stock_out", "خروج کالا از انبار")
    object Sales : Screen("sales", "ثبت فروش")
    object Purchases : Screen("purchases", "ثبت خرید")
    object Customers : Screen("customers", "مدیریت مشتریان")
    object Suppliers : Screen("suppliers", "مدیریت تأمین‌کنندگان")
    object Accounting : Screen("accounting", "حسابداری و امور مالی")
    object Expenses : Screen("expenses", "هزینه‌ها")
    object Incomes : Screen("incomes", "درآمدها")
    object Reports : Screen("reports", "گزارش‌ها و آمار")
    object History : Screen("history", "تاریخچه تراکنش‌ها")
    object Users : Screen("users", "مدیریت کاربران")
    object Backup : Screen("backup", "پشتیبان‌گیری و بازیابی")
    object Settings : Screen("settings", "تنظیمات برنامه")
    object AdminPanel : Screen("admin", "پنل مدیریت")
    object Login : Screen("login", "ورود مدیریت")
}

val drawerScreens = listOf(
    Screen.Dashboard, Screen.Products, Screen.Categories, Screen.StockIn, Screen.StockOut,
    Screen.Sales, Screen.Purchases, Screen.Customers, Screen.Suppliers, Screen.Accounting,
    Screen.Expenses, Screen.Incomes, Screen.Reports, Screen.History, Screen.Users,
    Screen.Backup, Screen.Settings, Screen.AdminPanel
)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    // Main scaffold with hamburger menu is inside each screen or wrap here
    NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) { DashboardScreen(navController) }
        composable(Screen.Products.route) { ProductsScreen(navController) }
        composable(Screen.Categories.route) { CategoriesScreen(navController) }
        composable(Screen.StockIn.route) { StockInScreen(navController) }
        composable(Screen.StockOut.route) { StockOutScreen(navController) }
        composable(Screen.Sales.route) { SalesScreen(navController) }
        composable(Screen.Purchases.route) { PurchasesScreen(navController) }
        composable(Screen.Customers.route) { CustomersScreen(navController) }
        composable(Screen.Suppliers.route) { SuppliersScreen(navController) }
        composable(Screen.Accounting.route) { AccountingScreen(navController) }
        composable(Screen.Expenses.route) { ExpensesScreen(navController) }
        composable(Screen.Incomes.route) { IncomesScreen(navController) }
        composable(Screen.Reports.route) { ReportsScreen(navController) }
        composable(Screen.History.route) { HistoryScreen(navController) }
        composable(Screen.Users.route) { UsersScreen(navController) }
        composable(Screen.Backup.route) { BackupScreen(navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
        composable(Screen.AdminPanel.route) { AdminPanelScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
    }
}
