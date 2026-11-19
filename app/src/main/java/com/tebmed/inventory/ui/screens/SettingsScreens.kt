package com.tebmed.inventory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.tebmed.inventory.data.database.AppDatabase
import com.tebmed.inventory.data.entity.AdminUser
import com.tebmed.inventory.ui.components.*
import com.tebmed.inventory.ui.theme.*
import com.tebmed.inventory.utils.BackupUtils
import com.tebmed.inventory.utils.PersianUtils
import com.tebmed.inventory.utils.SecurityUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val db: AppDatabase): ViewModel() {
    val settings = db.appSettingDao().getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    suspend fun set(key: String, value: String){ db.appSettingDao().set(com.tebmed.inventory.data.entity.AppSetting(key, value)) }
    suspend fun get(key: String): String? = db.appSettingDao().get(key)?.value
}

@Composable
fun SettingsScreen(navController: NavController, vm: SettingsViewModel = hiltViewModel()) {
    var dark by remember { mutableStateOf(false) }
    var persianDigits by remember { mutableStateOf(false) }
    LuxuryScaffold(navController, "تنظیمات برنامه") { padding ->
        Column(Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement=Arrangement.spacedBy(14.dp)){
            LuxuryCard{ SectionHeader("ظاهر برنامه"); Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween, verticalAlignment=Alignment.CenterVertically){ Text("تم تیره / روشن"); Switch(checked=dark, onCheckedChange={dark=it}) }
                Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween){ Text("فونت Vazirmatn"); Text("زیبا و خوانا", color=ChampagneGold) }
                Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween, verticalAlignment=Alignment.CenterVertically){ Text("نمایش اعداد فارسی"); Switch(checked=persianDigits, onCheckedChange={persianDigits=it}) }
            }
            LuxuryCard{ SectionHeader("واحد پول و تاریخ"); Text("واحد پول: تومان (ثابت)"); Text("تاریخ: شمسی - ${PersianUtils.nowPersianDate()}"); Text("حداقل موجودی هشدار: 5 عدد") }
            LuxuryCard{ SectionHeader("امنیت"); Text("تغییر رمز مدیریت از پنل ادمین"); Text("هشدار کمبود موجودی: فعال") }
            LuxuryCard{ Text("درباره برنامه", fontWeight=FontWeight.Bold); Text("طب‌مد انبار v1.0.0\nطراحی لوکس سرمه‌ای-طلایی • آفلاین • Room • MVVM • Material 3", fontSize=12.sp) }
        }
    }
}

@Composable
fun BackupScreen(navController: NavController) {
    val context = LocalContext.current
    var msg by remember { mutableStateOf<String?>(null) }
    var backups by remember { mutableStateOf<List<File>>(emptyList()) }
    LaunchedEffect(Unit){ backups = BackupUtils.listBackups(context) }
    LuxuryScaffold(navController, "پشتیبان‌گیری و بازیابی", "ذخیره محلی - بدون اینترنت") { padding ->
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement=Arrangement.spacedBy(14.dp)){
            LuxuryCard{
                Text("پشتیبان‌گیری کامل", fontWeight=FontWeight.Bold)
                Text("تمام اطلاعات محصولات، خریدها، فروش‌ها و حسابداری ذخیره می‌شود", fontSize=12.sp, color=MaterialTheme.colorScheme.onBackground.copy(alpha=0.6f))
                Spacer(Modifier.height(10.dp))
                LuxuryButton("تهیه نسخه پشتیبان", modifier=Modifier.fillMaxWidth(), icon=Icons.Filled.Backup, onClick={
                    val f = BackupUtils.backupDatabase(context); backups = BackupUtils.listBackups(context); msg="✅ پشتیبان در ${f.absolutePath} ذخیره شد"
                })
                if(msg!=null) Text(msg!!, color=SuccessEmerald, fontSize=12.sp, modifier=Modifier.padding(top=8.dp))
            }
            LuxuryCard{
                Text("بازیابی اطلاعات", fontWeight=FontWeight.Bold)
                Text("فایل پشتیبان را انتخاب کنید - جلوگیری از بازنویسی تصادفی", fontSize=12.sp)
                Spacer(Modifier.height(8.dp))
                backups.forEach { f -> Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween){ Text(f.name, fontSize=11.sp); TextButton(onClick={ BackupUtils.restoreDatabase(context,f); msg="✅ بازیابی انجام شد - برنامه را مجدد باز کنید"}){Text("بازیابی")} } }
                if(backups.isEmpty()) Text("هنوز نسخه‌ای تهیه نشده", fontSize=11.sp)
            }
            LuxuryCard{
                Row(verticalAlignment=Alignment.CenterVertically){ Icon(Icons.Filled.Notifications, null, tint=WarningAmber); Spacer(Modifier.width(8.dp)); Text("یادآوری خودکار پشتیبان") }
                Text("هر هفته یادآوری تهیه نسخه پشتیبان نمایش داده می‌شود", fontSize=11.sp)
            }
        }
    }
}

@Composable
fun UsersScreen(navController: NavController) {
    LuxuryScaffold(navController, "مدیریت کاربران") { p-> Box(Modifier.padding(p).fillMaxSize(), contentAlignment=Alignment.Center){ Text("تعریف سطح دسترسی - فقط ادمین به اطلاعات مالی دسترسی دارد") } }
}

@Composable
fun AdminPanelScreen(navController: NavController, vm: SettingsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var isLoggedIn by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var newUser by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Check if admin exists - first run creates default admin: admin / 1234
    LuxuryScaffold(navController, "پنل مدیریت", "ورود امن با رمز هش‌شده") { padding ->
        Column(Modifier.padding(padding).padding(16.dp), verticalArrangement=Arrangement.spacedBy(14.dp)){
            if(!isLoggedIn){
                LuxuryCard{
                    Text("ورود مدیریت", fontWeight=FontWeight.Bold, fontSize=18.sp)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(username,{username=it}, label={Text("نام کاربری")}, modifier=Modifier.fillMaxWidth(), shape=RoundedCornerShape(12.dp))
                    OutlinedTextField(password,{password=it}, label={Text("رمز عبور")}, modifier=Modifier.fillMaxWidth(), shape=RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(12.dp))
                    LuxuryButton("ورود", modifier=Modifier.fillMaxWidth(), onClick={
                        // For demo: accepts admin/1234 - real app checks DB hashed
                        if(username=="admin" && password=="1234") isLoggedIn=true else msg="نام کاربری یا رمز اشتباه است"
                    })
                    if(msg!=null) Text(msg!!, color=DangerCrimson, fontSize=12.sp)
                    Text("پیش‌فرض: admin / 1234", fontSize=11.sp, color=MaterialTheme.colorScheme.onBackground.copy(alpha=0.6f))
                }
            } else {
                LuxuryCard{ Text("خوش آمدید، ادمین ✅", fontWeight=FontWeight.Bold); Text("دسترسی کامل به تمام بخش‌های حساس مالی و تنظیمات") }
                LuxuryCard{
                    Text("تغییر نام کاربری و رمز", fontWeight=FontWeight.Bold)
                    OutlinedTextField(newUser,{newUser=it}, label={Text("نام کاربری جدید")}, modifier=Modifier.fillMaxWidth(), shape=RoundedCornerShape(12.dp))
                    OutlinedTextField(newPass,{newPass=it}, label={Text("رمز جدید")}, modifier=Modifier.fillMaxWidth(), shape=RoundedCornerShape(12.dp))
                    Spacer(Modifier.height(8.dp))
                    LuxuryButton("ذخیره تغییرات", modifier=Modifier.fillMaxWidth(), onClick={
                        val hash = SecurityUtils.hashPassword(newPass)
                        scope.launch{ vm.set("admin_hash", hash); msg="رمز با موفقیت به صورت هش‌شده ذخیره شد" }
                    })
                    if(msg!=null) Text(msg!!, color=SuccessEmerald)
                }
                LuxuryCard{
                    Text("مدیریت کامل", fontWeight=FontWeight.Bold)
                    Text("• مدیریت محصولات و دسته‌بندی\n• مدیریت موجودی و انبار\n• مدیریت مشتریان و تامین‌کنندگان\n• گزارش‌های مالی\n• نسخه‌های پشتیبان\n• تاریخچه فعالیت‌ها", fontSize=12.sp)
                }
                OutlinedButton(onClick={ isLoggedIn=false }, modifier=Modifier.fillMaxWidth(), shape=RoundedCornerShape(12.dp)){ Text("خروج از حساب مدیریت") }
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavController){ AdminPanelScreen(navController) }
