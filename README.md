# طب‌مد انبار - اپلیکیشن حسابداری و مدیریت انبار تجهیزات پزشکی

> **آفلاین • بدون اینترنت • بدون سرور • Room • Kotlin • Jetpack Compose • MVVM • Material 3**

## ✨ ویژگی‌های لوکس
- تم سرمه‌ای تیره + مشکی زغالی + طلایی شامپاینی + سفید ملایم
- فونت Vazirmatn، کامل RTL، تاریخ شمسی، تومان
- کارت‌های 20dp گرد + سایه نرم + گرادینت + انیمیشن
- منوی همبرگری Drawer با انیمیشن روان
- نمودارهای فروش/سود (Canvas Compose)

## 📦 ساختار فنی
- **زبان:** Kotlin 1.9.22
- **UI:** Jetpack Compose + Material3 + Navigation Compose
- **DB:** Room 2.6.1 (10 Entity) - ذخیره محلی دائمی
- **DI:** Hilt
- **امنیت:** BCrypt هش رمز ادمین
- **گزارش:** iText PDF + Apache POI Excel + OpenCSV
- **تاریخ:** PersianDate (سامان زمانی)

## 🚀 نصب و ساخت APK

### روش 1: Android Studio
1. پروژه را باز کنید
2. `Sync Project with Gradle Files`
3. `Build > Build APK(s)` یا `./gradlew assembleDebug`
4. فایل در `app/build/outputs/apk/debug/app-debug.apk`

### روش 2: GitHub Actions (اتوماتیک)
پوشه `.github/workflows/build.yml` موجود است - هر Push یک APK می‌سازد.

```bash
./gradlew assembleDebug
./gradlew assembleRelease  # نیاز به keystore
```

### روش 3: خط فرمان بدون Android Studio
```bash
# نصب cmdline-tools
sdkmanager --install "platform-tools" "platforms;android-34" "build-tools;34.0.0"
./gradlew assembleDebug
```

## 🔐 پنل ادمین
- پیش‌فرض: `admin / 1234`
- رمز با BCrypt ذخیره می‌شود
- فقط ادمین به مالی/حذف/موجودی منفی دسترسی دارد

## 📊 محاسبه سود دقیق
میانگین موزون بهای تمام‌شده:
```
newAvg = (oldQty * oldAvg + newQty * newPrice) / newTotalQty
profit = saleAmount - (saleQty * weightedAvg)
```

## 💾 پشتیبان‌گیری
- `TebMedBackups/backup_*.db` در حافظه خارجی
- کپی فایل SQLite، بازیابی با جایگزینی فایل
- یادآوری هفتگی

## 📋 چک‌لیست تحویل
- [x] تمام 18 آیتم منو فعال
- [x] CRUD محصول با 5 فیلد ساده
- [x] ورود/خروج با تایید ادمین برای منفی
- [x] حسابداری 5 روش پرداخت
- [x] گزارش + خروجی PDF/Excel/CSV شمسی
- [x] جستجو و فیلتر
- [x] پشتیبان‌گیری Room
- [x] تم روشن/تیره، RTL، اعداد فارسی

## 📱 حداقل نیاز
Android 7.0 (API 24) به بالا - تمام سایز صفحه - آفلاین کامل

---
ساخته شده با ❤️ برای فروشگاه تجهیزات پزشکی
