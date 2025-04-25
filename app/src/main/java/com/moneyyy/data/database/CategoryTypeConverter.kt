package com.moneyyy.data.database

import androidx.room.TypeConverter
import com.moneyyy.data.model.CategoryType

class CategoryTypeConverter {
    @TypeConverter
    fun toCategoryType(value: String): CategoryType = CategoryType.valueOf(value)

    @TypeConverter
    fun fromCategoryType(value: CategoryType): String = value.name
}