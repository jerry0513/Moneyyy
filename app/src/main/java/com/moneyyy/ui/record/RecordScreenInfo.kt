package com.moneyyy.ui.record

import com.moneyyy.data.price.PriceInfo

data class RecordScreenInfo(
    val category: String,
    val priceInfo: PriceInfo,
    val date: String,
    val note: String
)
