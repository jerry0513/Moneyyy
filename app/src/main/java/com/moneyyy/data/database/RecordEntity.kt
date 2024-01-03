package com.moneyyy.data.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moneyyy.data.Millis
import com.moneyyy.data.price.PriceType

@Entity
data class RecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "date") val date: Millis,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "note") val note: String,
    @Embedded(prefix = "price_info_") val priceInfo: RecordPriceInfoEntity
)

@Entity
data class RecordPriceInfoEntity(
    @ColumnInfo(name = "type") val type: PriceType,
    @ColumnInfo(name = "price") val price: Int,
)
