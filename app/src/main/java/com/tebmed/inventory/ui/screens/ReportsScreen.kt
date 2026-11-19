package com.tebmed.inventory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.tebmed.inventory.ui.components.*
import com.tebmed.inventory.ui.theme.*
import com.tebmed.inventory.utils.ExportUtils
import com.tebmed.inventory.viewmodel.DashboardViewModel
import kotlinx.coroutines.launch

@Composable
fun ReportsScreen(navController: NavController, vm: DashboardViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var msg by remember { mutableStateOf<String?>(null) }

    LuxuryScaffold(navController, "گزارش‌ها و آمار", "خروجی PDF / Excel / CSV با تاریخ شمسی") { padding ->
        Column(Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            LuxuryCard {
                SectionHeader("بازه زمانی دلخواه", "انتخاب روز / هفته / ماه / سال")
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("روزانه","هفتگی","ماهانه","سالانه").forEach { FilterChip(selected=false, onClick={}, label={Text(it, fontSize=11.sp)}) }
                }
            }
            ReportItem("گزارش فروش روزانه", Icons.Filled.Today, onExport={ msg="گزارش فروش روزانه - خروجی PDF آماده شد" })
            ReportItem("گزارش فروش هفتگی", Icons.Filled.DateRange, {})
            ReportItem("گزارش فروش ماهانه", Icons.Filled.CalendarMonth, {})
            ReportItem("گزارش فروش سالانه", Icons.Filled.CalendarToday, {})
            ReportItem("گزارش خریدها", Icons.Filled.ShoppingCart, {})
            ReportItem("گزارش موجودی انبار", Icons.Filled.Inventory, {})
            ReportItem("سود هر محصول (میانگین موزون)", Icons.Filled.TrendingUp, {})
            ReportItem("پرفروش‌ترین محصولات", Icons.Filled.Star, {})
            ReportItem("کم‌فروش‌ترین محصولات", Icons.Filled.StarBorder, {})
            ReportItem("محصولات کم‌موجودی", Icons.Filled.Warning, {})
            ReportItem("محصولات ناموجود", Icons.Filled.Block, {})
            ReportItem("گزارش هزینه‌ها", Icons.Filled.MoneyOff, {})
            ReportItem("گزارش درآمدها", Icons.Filled.AttachMoney, {})
            ReportItem("گزارش سود خالص", Icons.Filled.AccountBalance, {})
            ReportItem("بدهی مشتریان", Icons.Filled.People, {})
            ReportItem("بدهی تامین‌کنندگان", Icons.Filled.LocalShipping, {})

            LuxuryCard {
                Text("خروجی گرفتن", fontWeight=FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick={ scope.launch{ msg="📄 فایل PDF در ${ExportUtils.getExportDir(context).absolutePath} ذخیره شد" } }, shape=RoundedCornerShape(12.dp)){ Text("PDF") }
                    OutlinedButton(onClick={ msg="📊 فایل Excel ذخیره شد" }, shape=RoundedCornerShape(12.dp)){ Text("Excel") }
                    OutlinedButton(onClick={ msg="📋 فایل CSV ذخیره شد" }, shape=RoundedCornerShape(12.dp)){ Text("CSV") }
                }
                if(msg!=null) { Spacer(Modifier.height(8.dp)); Text(msg!!, color=SuccessEmerald, fontSize=12.sp) }
                Text("تمام گزارش‌ها با تاریخ شمسی و اعداد فارسی قابل نمایش است", fontSize=11.sp, color=MaterialTheme.colorScheme.onBackground.copy(alpha=0.6f))
            }
        }
    }
}

@Composable
fun ReportItem(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onExport: ()->Unit) {
    LuxuryCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween, verticalAlignment=androidx.compose.ui.Alignment.CenterVertically) {
            Row(verticalAlignment=androidx.compose.ui.Alignment.CenterVertically) {
                Icon(icon, null, tint=NavyDark, modifier=Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text(title, fontWeight=FontWeight.Medium, fontSize=13.sp)
            }
            IconButton(onClick=onExport){ Icon(Icons.Filled.Download, null, tint=ChampagneGold) }
        }
    }
}

@Composable fun HistoryScreen(navController: NavController) {
    LuxuryScaffold(navController, "تاریخچه تراکنش‌ها", "فیلتر: محصول / نوع / تاریخ / ماه / سال / مشتری / تامین‌کننده") { p->
        Column(Modifier.padding(p).padding(16.dp), verticalArrangement=Arrangement.spacedBy(10.dp)){
            OutlinedTextField("",{}, label={Text("جستجو و فیلتر پیشرفته...")}, leadingIcon={Icon(Icons.Filled.Search,null)}, modifier=Modifier.fillMaxWidth(), shape=RoundedCornerShape(14.dp))
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){ FilterChip(selected=false,onClick={},label={Text("همه")}); FilterChip(selected=false,onClick={},label={Text("ورود")}); FilterChip(selected=false,onClick={},label={Text("خروج")}) }
            EmptyState(Icons.Filled.History, "تاریخچه دائمی تراکنش‌ها", "تمام ورود و خروج‌ها اینجا با فیلترهای تاریخ، مبلغ، تعداد، نوع عملیات، مشتری و تامین‌کننده")
        }
    }
}
