package com.tebmed.inventory.utils

import androidx.room.TypeConverter
import com.tebmed.inventory.data.entity.PaymentMethod
import com.tebmed.inventory.data.entity.TransactionType

class Converters {
    @TypeConverter fun fromTransactionType(v: TransactionType): String = v.name
    @TypeConverter fun toTransactionType(v: String): TransactionType = TransactionType.valueOf(v)

    @TypeConverter fun fromPaymentMethod(v: PaymentMethod): String = v.name
    @TypeConverter fun toPaymentMethod(v: String): PaymentMethod = PaymentMethod.valueOf(v)
}
