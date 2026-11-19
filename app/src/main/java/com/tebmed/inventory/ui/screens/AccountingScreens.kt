package com.tebmed.inventory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.tebmed.inventory.data.database.AppDatabase
import com.tebmed.inventory.data.entity.Expense
import com.tebmed.inventory.data.entity.Income
import com.tebmed.inventory.ui.components.*
import com.tebmed.inventory.ui.theme.*
import com.tebmed.inventory.utils.PersianUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountingViewModel @Inject constructor(private val db: AppDatabase): ViewModel() {
    val expenses = db.expenseDao().getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val incomes = db.incomeDao().getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    suspend fun addExpense(title: String, amount: Long, category: String?) {
        db.expenseDao().insert(Expense(title=title, amount=amount, category=category, persianDate=PersianUtils.nowPersianDate()))
    }
    suspend fun addIncome(title: String, amount: Long) {
        db.incomeDao().insert(Income(title=title, amount=amount, persianDate=PersianUtils.nowPersianDate()))
    }
    suspend fun deleteExpense(id: Long) = db.expenseDao().deleteById(id)
}

@Composable
fun AccountingScreen(navController: NavController, vm: AccountingViewModel = hiltViewModel()) {
    val expenses by vm.expenses.collectAsState()
    val incomes by vm.incomes.collectAsState()
    LuxuryScaffold(navController, "حسابداری و امور مالی", "سود دقیق بر اساس میانگین موزون") { padding ->
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LuxuryCard {
                SectionHeader("خلاصه مالی", "محاسبه میانگین موزون بهای تمام‌شده")
                Text("سود هر محصول = مجموع فروش - (تعداد فروش × میانگین موزون خرید)", fontSize=11.sp, color=MaterialTheme.colorScheme.onBackground.copy(alpha=0.6f))
            }
            Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                LuxuryCard(modifier=Modifier.weight(1f)){ Text("کل هزینه‌ها", fontSize=12.sp); TomanText(expenses.sumOf{it.amount}, 14.sp, true, DangerCrimson) }
                LuxuryCard(modifier=Modifier.weight(1f)){ Text("کل درآمدها", fontSize=12.sp); TomanText(incomes.sumOf{it.amount}, 14.sp, true, SuccessEmerald) }
            }
            Text("روش‌های پرداخت: نقد، کارت‌خوان، انتقال بانکی، ترکیبی، نسیه - همه قابل ثبت است", fontSize=11.sp)
        }
    }
}

@Composable
fun ExpensesScreen(navController: NavController, vm: AccountingViewModel = hiltViewModel()) {
    val expenses by vm.expenses.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    LuxuryScaffold(navController, "هزینه‌ها", "ثبت اجاره، حمل، حقوق و...", floatingActionButton = { FloatingActionButton(onClick={showAdd=true}, containerColor=NavyDark, contentColor=ChampagneGold){ Icon(Icons.Filled.Add,null)} }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            LazyColumn(verticalArrangement=Arrangement.spacedBy(10.dp)) {
                items(expenses) { e ->
                    LuxuryCard {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement= Arrangement.SpaceBetween) {
                            Column{ Text(e.title, fontWeight=FontWeight.Bold); Text(e.persianDate, fontSize=11.sp) }
                            TomanText(e.amount, 14.sp, true, DangerCrimson)
                        }
                    }
                }
                if(expenses.isEmpty()) item{ EmptyState(Icons.Filled.MoneyOff, "هزینه‌ای ثبت نشده", "هزینه‌های خود را اینجا ثبت کنید") }
            }
        }
        if(showAdd) AlertDialog(onDismissRequest={showAdd=false}, title={Text("افزودن هزینه")}, text={
            Column(verticalArrangement=Arrangement.spacedBy(8.dp)){
                OutlinedTextField(title,{title=it}, label={Text("عنوان")}, modifier=Modifier.fillMaxWidth(), shape=RoundedCornerShape(12.dp))
                OutlinedTextField(amount,{amount=it}, label={Text("مبلغ (تومان)")}, modifier=Modifier.fillMaxWidth(), shape=RoundedCornerShape(12.dp))
            }
        }, confirmButton={ LuxuryButton("ثبت",{ scope.launch{ vm.addExpense(title, amount.toLongOrNull()?:0, null); showAdd=false; title=""; amount="" } }) }, dismissButton={ TextButton(onClick={showAdd=false}){Text("انصراف")} }, shape=RoundedCornerShape(20.dp))
    }
}

@Composable
fun IncomesScreen(navController: NavController, vm: AccountingViewModel = hiltViewModel()) {
    val incomes by vm.incomes.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    LuxuryScaffold(navController, "درآمدها", floatingActionButton={ FloatingActionButton(onClick={showAdd=true}, containerColor=NavyDark, contentColor=ChampagneGold){ Icon(Icons.Filled.Add,null)} }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(16.dp)){
            LazyColumn(verticalArrangement=Arrangement.spacedBy(10.dp)){
                items(incomes){ i-> LuxuryCard{ Row(Modifier.fillMaxWidth(), horizontalArrangement= Arrangement.SpaceBetween){ Column{ Text(i.title, fontWeight=FontWeight.Bold); Text(i.persianDate, fontSize=11.sp)}; TomanText(i.amount,14.sp,true,SuccessEmerald) } } }
                if(incomes.isEmpty()) item{ EmptyState(Icons.Filled.AttachMoney, "درآمدی ثبت نشده", "") }
            }
        }
        if(showAdd) AlertDialog(onDismissRequest={showAdd=false}, title={Text("افزودن درآمد")}, text={
            Column(verticalArrangement=Arrangement.spacedBy(8.dp)){
                OutlinedTextField(title,{title=it}, label={Text("عنوان")}, modifier=Modifier.fillMaxWidth(), shape=RoundedCornerShape(12.dp))
                OutlinedTextField(amount,{amount=it}, label={Text("مبلغ")}, modifier=Modifier.fillMaxWidth(), shape=RoundedCornerShape(12.dp))
            }
        }, confirmButton={ LuxuryButton("ثبت",{ scope.launch{ vm.addIncome(title, amount.toLongOrNull()?:0); showAdd=false } }) }, dismissButton={ TextButton(onClick={showAdd=false}){Text("انصراف")} }, shape=RoundedCornerShape(20.dp))
    }
}
