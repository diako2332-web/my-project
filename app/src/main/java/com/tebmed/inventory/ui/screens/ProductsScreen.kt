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
import com.tebmed.inventory.data.entity.Product
import com.tebmed.inventory.ui.components.*
import com.tebmed.inventory.ui.theme.*
import com.tebmed.inventory.utils.PersianUtils
import com.tebmed.inventory.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(navController: NavController, vm: ProductViewModel = hiltViewModel()) {
    val products by vm.filteredProducts.collectAsState()
    val search by vm.searchQuery.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHost = remember { SnackbarHostState() }

    LuxuryScaffold(
        navController = navController,
        title = "مدیریت محصولات",
        subtitle = "${products.size} محصول",
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = NavyDark, contentColor = ChampagneGold) {
                Icon(Icons.Filled.Add, null)
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            OutlinedTextField(
                value = search,
                onValueChange = vm::setSearch,
                placeholder = { Text("جستجو بر اساس نام محصول...") },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            if (products.isEmpty()) {
                EmptyState(Icons.Filled.Inventory2, "محصولی یافت نشد", "اولین محصول خود را اضافه کنید")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(products, key = { it.id }) { p ->
                        ProductCard(p, onDelete = { scope.launch { vm.deleteProduct(p.id) } })
                    }
                }
            }
        }
        if (showAddDialog) AddProductDialog(onDismiss = { showAddDialog = false }, onAdd = { name, stock, pur, whole, supp ->
            scope.launch {
                val res = vm.addProduct(name, stock, pur, whole, supp)
                if (res.isSuccess) showAddDialog = false
            }
        })
    }
}

@Composable
fun ProductCard(p: Product, onDelete: () -> Unit) {
    LuxuryCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(p.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("موجودی: ${PersianUtils.formatNumberInt(p.stockQuantity)} عدد", fontSize = 12.sp, color = if(p.stockQuantity==0) DangerCrimson else SuccessEmerald)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = {}, label = { Text(PersianUtils.formatToman(p.purchasePrice), fontSize = 11.sp) }, leadingIcon = { Icon(Icons.Filled.ShoppingCart, null, Modifier.size(14.dp)) })
                    AssistChip(onClick = {}, label = { Text(PersianUtils.formatToman(p.wholesalePrice), fontSize = 11.sp) }, leadingIcon = { Icon(Icons.Filled.Sell, null, Modifier.size(14.dp)) })
                }
                if (p.supplierName != null) Text("تامین‌کننده: ${p.supplierName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha=0.6f))
            }
            IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, null, tint = DangerCrimson) }
        }
    }
}

@Composable
fun AddProductDialog(onDismiss: () -> Unit, onAdd: (String, Int, Long, Long, String?) -> Unit) {
    var name by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var pur by remember { mutableStateOf("") }
    var whole by remember { mutableStateOf("") }
    var supplier by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("افزودن محصول جدید", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("فقط فیلدهای مشخص‌شده نمایش داده می‌شود", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha=0.6f))
                OutlinedTextField(name, { name = it }, label = { Text("نام محصول *") }, singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(stock, { stock = it }, label = { Text("تعداد موجودی فعلی") }, singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(pur, { pur = it }, label = { Text("قیمت خرید (تومان)") }, singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(whole, { whole = it }, label = { Text("قیمت عمده‌فروشی (تومان)") }, singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(supplier, { supplier = it }, label = { Text("نام تامین‌کننده (اختیاری)") }, singleLine = true, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            LuxuryButton("افزودن", onClick = {
                onAdd(name, stock.toIntOrNull() ?: 0, pur.toLongOrNull() ?: 0, whole.toLongOrNull() ?: 0, supplier.ifBlank { null })
            })
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } },
        shape = RoundedCornerShape(20.dp)
    )
}

// Placeholder screens - categories, stock, etc. simple wrappers
@Composable fun CategoriesScreen(navController: NavController) {
    LuxuryScaffold(navController, "دسته‌بندی محصولات") { p -> Box(Modifier.padding(p).fillMaxSize(), contentAlignment = Alignment.Center) { Text("مدیریت دسته‌بندی‌ها - کولار، کمربند، زانوبند و...") } }
}
