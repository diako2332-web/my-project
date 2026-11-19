package com.tebmed.inventory.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.tebmed.inventory.ui.components.*
import com.tebmed.inventory.ui.theme.*
import com.tebmed.inventory.utils.PersianUtils
import com.tebmed.inventory.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(navController: NavController, vm: DashboardViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()

    LuxuryScaffold(navController = navController, title = "داشبورد اصلی", subtitle = PersianUtils.nowPersianDate()) {
        padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Greeting Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Row(modifier = Modifier.padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("سلام، مدیر عزیز 👋", color = SoftWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("نمای کلی انبار و حساب‌های شما", color = SoftWhite.copy(alpha=0.7f), fontSize = 13.sp)
                    }
                    Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(ChampagneGold), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Icon(Icons.Filled.Store, null, tint = NavyDark)
                    }
                }
            }

            // Stats Grid 2 columns
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("کل محصولات", PersianUtils.formatNumberInt(state.totalProducts), Icons.Filled.Inventory2, listOf(NavyDark, NavyMedium), modifier = Modifier.weight(1f))
                StatCard("کم‌موجودی", PersianUtils.formatNumberInt(state.lowStockCount), Icons.Filled.Warning, listOf(WarningAmber, Color(0xFFD97706)), modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("ارزش خرید انبار", PersianUtils.formatToman(state.inventoryPurchaseValue), Icons.Filled.AccountBalanceWallet, listOf(NavyDeep, NavyLight), modifier = Modifier.weight(1f))
                StatCard("ارزش فروش انبار", PersianUtils.formatToman(state.inventoryWholesaleValue), Icons.Filled.TrendingUp, listOf(CharcoalBlack, CharcoalLight), modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LuxuryCard(modifier = Modifier.weight(1f)) {
                    Text("مجموع فروش", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha=0.6f))
                    TomanText(state.totalSales, 16.sp, true, SuccessEmerald)
                }
                LuxuryCard(modifier = Modifier.weight(1f)) {
                    Text("مجموع خرید", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha=0.6f))
                    TomanText(state.totalPurchases, 16.sp, true, DangerCrimson)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LuxuryCard(modifier = Modifier.weight(1f)) {
                    Text("سود ناخالص", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha=0.6f))
                    TomanText(state.totalProfit, 16.sp, true, if(state.totalProfit>=0) SuccessEmerald else DangerCrimson)
                }
                LuxuryCard(modifier = Modifier.weight(1f)) {
                    Text("هزینه‌ها", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha=0.6f))
                    TomanText(state.totalExpenses, 16.sp, true, WarningAmber)
                }
            }
            LuxuryCard {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column { Text("سود خالص", fontWeight = FontWeight.Bold); Text("پس از کسر هزینه‌ها", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha=0.6f)) }
                    TomanText(state.netProfit, 19.sp, true, if(state.netProfit>=0) NavyDark else DangerCrimson)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LuxuryCard(modifier = Modifier.weight(1f)) {
                    Text("بدهی مشتریان", fontSize = 12.sp); TomanText(state.customerDebts, 14.sp, true, DangerCrimson)
                }
                LuxuryCard(modifier = Modifier.weight(1f)) {
                    Text("بدهی به تامین‌کنندگان", fontSize = 12.sp); TomanText(state.supplierDebts, 14.sp, true, WarningAmber)
                }
            }

            // Chart placeholder luxury
            LuxuryCard {
                SectionHeader("نمودار فروش و سود", "نمایش گرافیکی لوکس")
                Spacer(Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(14.dp)).background(NavyDark.copy(alpha=0.05f)), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("📊 نمودار فروش روزانه / ماهانه - به‌روزرسانی خودکار پس از هر تراکنش", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha=0.5f))
                }
            }

            Text("آخرین تراکنش‌ها به‌صورت خودکار اینجا نمایش داده می‌شود", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha=0.5f))
        }
    }
}
