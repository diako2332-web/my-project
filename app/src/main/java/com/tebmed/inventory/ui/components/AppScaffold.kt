package com.tebmed.inventory.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tebmed.inventory.ui.navigation.Screen
import com.tebmed.inventory.ui.navigation.drawerScreens
import com.tebmed.inventory.ui.theme.*
import kotlinx.coroutines.launch

data class DrawerItemData(val screen: Screen, val icon: ImageVector)

fun drawerIconFor(route: String): ImageVector = when(route) {
    Screen.Dashboard.route -> Icons.Filled.Dashboard
    Screen.Products.route -> Icons.Filled.Inventory2
    Screen.Categories.route -> Icons.Filled.Category
    Screen.StockIn.route -> Icons.Filled.Input
    Screen.StockOut.route -> Icons.Filled.Output
    Screen.Sales.route -> Icons.Filled.PointOfSale
    Screen.Purchases.route -> Icons.Filled.ShoppingCart
    Screen.Customers.route -> Icons.Filled.People
    Screen.Suppliers.route -> Icons.Filled.LocalShipping
    Screen.Accounting.route -> Icons.Filled.AccountBalance
    Screen.Expenses.route -> Icons.Filled.MoneyOff
    Screen.Incomes.route -> Icons.Filled.AttachMoney
    Screen.Reports.route -> Icons.Filled.BarChart
    Screen.History.route -> Icons.Filled.History
    Screen.Users.route -> Icons.Filled.ManageAccounts
    Screen.Backup.route -> Icons.Filled.Backup
    Screen.Settings.route -> Icons.Filled.Settings
    Screen.AdminPanel.route -> Icons.Filled.AdminPanelSettings
    else -> Icons.Filled.Circle
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuxuryScaffold(
    navController: NavController,
    title: String,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = NavyDark,
                drawerContentColor = SoftWhite,
                modifier = Modifier.width(300.dp)
            ) {
                // Header with gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Brush.linearGradient(listOf(NavyDark, NavyDeep, CharcoalBlack)))
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.BottomStart)) {
                        Box(
                            modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(ChampagneGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("ط", color = NavyDark, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("طب‌مد انبار", color = SoftWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("سیستم مدیریت تجهیزات پزشکی", color = SoftWhite.copy(alpha=0.7f), fontSize = 12.sp)
                        Text("نسخه 1.0.0 • آفلاین", color = ChampagneGold, fontSize = 11.sp)
                    }
                }
                Divider(color = SoftWhite.copy(alpha=0.1f))
                LazyColumn(modifier = Modifier.fillMaxSize().padding(vertical = 8.dp)) {
                    items(drawerScreens) { screen ->
                        val isSelected = false // could track current route
                        NavigationDrawerItem(
                            icon = { Icon(drawerIconFor(screen.route), null, tint = if(isSelected) ChampagneGold else SoftWhite.copy(alpha=0.85f)) },
                            label = { Text(screen.title, color = if(isSelected) ChampagneGold else SoftWhite, fontSize = 13.sp) },
                            selected = isSelected,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate(screen.route) { launchSingleTop = true }
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = SoftWhite.copy(alpha=0.08f),
                                unselectedContainerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            if (subtitle != null) Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha=0.6f))
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "منو", tint = NavyDark)
                        }
                    },
                    actions = {
                        Row(verticalAlignment = Alignment.CenterVertically, content = actions)
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            floatingActionButton = floatingActionButton,
            containerColor = MaterialTheme.colorScheme.background,
            content = content
        )
    }
}
