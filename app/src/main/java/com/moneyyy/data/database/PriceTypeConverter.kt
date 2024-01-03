package com.moneyyy.data.database

import androidx.room.TypeConverter
import com.moneyyy.data.price.PriceType

class PriceTypeConverter {
    @TypeConverter
    fun toPriceType(value: String): PriceType {
        return PriceType.valueOf(value)
    }

    @TypeConverter
    fun fromPriceType(value: PriceType): String {
        return value.name
    }
}