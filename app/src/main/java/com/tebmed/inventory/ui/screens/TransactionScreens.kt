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
import androidx.navigation.NavController
import com.tebmed.inventory.data.entity.PaymentMethod
import com.tebmed.inventory.ui.components.*
import com.tebmed.inventory.ui.theme.*
import com.tebmed.inventory.utils.PersianUtils
import com.tebmed.inventory.utils.SecurityUtils
import com.tebmed.inventory.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

@Composable
fun StockInScreen(navController: NavController, vm: ProductViewModel = hiltViewModel()) {
    val products by vm.products.collectAsState()
    var selectedId by remember { mutableStateOf<Long?>(null) }
    var qty by remember { mutableStateOf("") }
    var unitPrice by remember { mutableStateOf("") }
    var supplier by remember { mutableStateOf("") }
    var invoice by remember { mutableStateOf(SecurityUtils.generatePurchaseInvoice()) }
    var desc by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    val scope = rememberCoroutineScope()
    var msg by remember { mutableStateOf<String?>(null) }

    LuxuryScaffold(navController, "ورود کالا به انبار", "ثبت خرید و افزایش موجودی") { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LuxuryCard {
                Text("انتخاب محصول", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                var expanded by remember { mutableStateOf(false) }
                val selectedName = products.find { it.id == selectedId }?.name ?: "انتخاب کنید"
                ExposedDropdownMenuBox(expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(selectedName, {}, readOnly = true, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.menuAnchor().fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    ExposedDropdownMenu(expanded, onDismissRequest = { expanded = false }) {
                        products.forEach { p -> DropdownMenuItem(text = { Text(p.name) }, onClick = { selectedId = p.id; expanded = false }) }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(qty, { qty = it }, label = { Text("تعداد") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                OutlinedTextField(unitPrice, { unitPrice = it }, label = { Text("قیمت واحد (تومان)") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
            }
            val total = (qty.toLongOrNull() ?: 0) * (unitPrice.toLongOrNull() ?: 0)
            if (total > 0) Text("مبلغ کل: ${PersianUtils.formatToman(total)}", fontWeight = FontWeight.Bold, color = NavyDark)
            OutlinedTextField(supplier, { supplier = it }, label = { Text("نام تامین‌کننده") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(invoice, { invoice = it }, label = { Text("شماره فاکتور خرید") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(desc, { desc = it }, label = { Text("توضیحات اضافی") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            // Payment method chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PaymentMethod.values().forEach { pm ->
                    FilterChip(selected = paymentMethod==pm, onClick = { paymentMethod=pm }, label = { Text(pm.name, fontSize=11.sp) })
                }
            }
            LuxuryButton("ثبت ورود کالا", onClick = {
                scope.launch {
                    if (selectedId==null) { msg="محصول را انتخاب کنید"; return@launch }
                    val res = vm.stockIn(selectedId!!, qty.toIntOrNull()?:0, unitPrice.toLongOrNull()?:0, supplier.ifBlank{null}, invoice, paymentMethod, desc.ifBlank{null})
                    msg = if(res.isSuccess) "✅ ورود کالا با موفقیت ثبت شد - موجودی به‌روزرسانی شد" else res.exceptionOrNull()?.message
                }
            }, modifier = Modifier.fillMaxWidth(), icon = Icons.Filled.Input)
            if (msg != null) Card(colors = CardDefaults.cardColors(containerColor = SuccessEmerald.copy(alpha=0.15f))) { Text(msg!!, modifier = Modifier.padding(12.dp), color = SuccessEmerald) }
        }
    }
}

@Composable
fun StockOutScreen(navController: NavController, vm: ProductViewModel = hiltViewModel()) {
    val products by vm.products.collectAsState()
    var selectedId by remember { mutableStateOf<Long?>(null) }
    var qty by remember { mutableStateOf("") }
    var unitPrice by remember { mutableStateOf("") }
    var customer by remember { mutableStateOf("") }
    var invoice by remember { mutableStateOf(SecurityUtils.generateInvoiceNumber()) }
    var paymentMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var showAdminConfirm by remember { mutableStateOf(false) }
    var pendingOverride by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var msg by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    LuxuryScaffold(navController, "خروج کالا از انبار", "ثبت فروش و کاهش موجودی") { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LuxuryCard {
                Text("انتخاب محصول", fontWeight = FontWeight.Bold)
                var expanded by remember { mutableStateOf(false) }
                val sel = products.find { it.id==selectedId }
                ExposedDropdownMenuBox(expanded, onExpandedChange = { expanded=!expanded }) {
                    OutlinedTextField(sel?.name ?: "انتخاب کنید", {}, readOnly=true, trailingIcon={ExposedDropdownMenuDefaults.TrailingIcon(expanded)}, modifier=Modifier.menuAnchor().fillMaxWidth(), shape=RoundedCornerShape(12.dp))
                    ExposedDropdownMenu(expanded, onDismissRequest={expanded=false}) { products.forEach{ p-> DropdownMenuItem(text={Text("${p.name} - موجودی: ${p.stockQuantity}")}, onClick={selectedId=p.id; expanded=false}) } }
                }
                if(sel!=null) Text("موجودی فعلی: ${sel.stockQuantity}", fontSize=12.sp, color = if((qty.toIntOrNull()?:0) > sel.stockQuantity) DangerCrimson else SuccessEmerald)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(qty, {qty=it}, label={Text("تعداد")}, modifier=Modifier.weight(1f), shape=RoundedCornerShape(12.dp))
                OutlinedTextField(unitPrice, {unitPrice=it}, label={Text("قیمت فروش (تومان)")}, modifier=Modifier.weight(1f), shape=RoundedCornerShape(12.dp))
            }
            OutlinedTextField(customer, {customer=it}, label={Text("نام مشتری (اختیاری)")}, modifier=Modifier.fillMaxWidth(), shape=RoundedCornerShape(12.dp))
            OutlinedTextField(invoice, {invoice=it}, label={Text("شماره فاکتور")}, modifier=Modifier.fillMaxWidth(), shape=RoundedCornerShape(12.dp))
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) { PaymentMethod.values().forEach{ pm-> FilterChip(selected=paymentMethod==pm, onClick={paymentMethod=pm}, label={Text(pm.name, fontSize=11.sp)}) } }
            LuxuryButton("ثبت خروج کالا", modifier=Modifier.fillMaxWidth(), icon=Icons.Filled.Output, onClick={
                scope.launch {
                    if(selectedId==null){ msg="محصول را انتخاب کنید"; isError=true; return@launch }
                    val res = vm.stockOut(selectedId!!, qty.toIntOrNull()?:0, unitPrice.toLongOrNull()?:0, customer.ifBlank{null}, invoice, paymentMethod, null, adminOverride = pendingOverride)
                    if(res.isSuccess){ msg="✅ خروج کالا ثبت شد"; isError=false; pendingOverride=false }
                    else {
                        val err=res.exceptionOrNull()?.message ?: ""
                        if(err.contains("تایید ادمین")) { showAdminConfirm=true } else { msg=err; isError=true }
                    }
                }
            })
            if(msg!=null) Card(colors=CardDefaults.cardColors(containerColor= if(isError) DangerCrimson.copy(alpha=0.12f) else SuccessEmerald.copy(alpha=0.15f))){ Text(msg!!, Modifier.padding(12.dp), color=if(isError) DangerCrimson else SuccessEmerald) }
        }
        if(showAdminConfirm) AlertDialog(
            onDismissRequest={showAdminConfirm=false},
            title={Text("هشدار موجودی منفی")},
            text={Text("موجودی کافی نیست. آیا با تایید ادمین می‌خواهید ادامه دهید؟ این عملیات ثبت می‌شود.")},
            confirmButton={ Button(onClick={ pendingOverride=true; showAdminConfirm=false; msg="مجدد تلاش کنید (با تایید ادمین)"; isError=false }, colors=ButtonDefaults.buttonColors(containerColor=DangerCrimson)){ Text("تایید ادمین") } },
            dismissButton={ TextButton(onClick={showAdminConfirm=false}){ Text("انصراف") } }
        )
    }
}

@Composable fun SalesScreen(navController: NavController) { StockOutScreen(navController) }
@Composable fun PurchasesScreen(navController: NavController) { StockInScreen(navController) }

@Composable fun CustomersScreen(navController: NavController) {
    LuxuryScaffold(navController, "مدیریت مشتریان") { p-> Box(Modifier.padding(p).fillMaxSize().padding(16.dp)){ Text("افزودن، ویرایش، حذف مشتری + بدهی و تاریخچه خرید هر مشتری") } }
}
@Composable fun SuppliersScreen(navController: NavController) {
    LuxuryScaffold(navController, "مدیریت تأمین‌کنندگان") { p-> Box(Modifier.padding(p).fillMaxSize().padding(16.dp)){ Text("تامین‌کنندگان + بدهی به تامین‌کننده + تاریخچه خرید از هر تامین‌کننده") } }
}
