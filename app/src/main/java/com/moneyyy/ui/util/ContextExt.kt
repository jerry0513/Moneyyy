package com.moneyyy.ui.util

import android.content.Context
import android.widget.Toast

fun Context.showToast(
    resId: Int,
    duration: Int = Toast.LENGTH_SHORT
) {
    Toast.makeText(this, resId, duration)
        .show()
}