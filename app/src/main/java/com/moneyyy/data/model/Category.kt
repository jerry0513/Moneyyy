package com.moneyyy.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.moneyyy.R

interface Category {
    val name: String
    val nameResId: Int
    val iconResId: Int
}

enum class ExpenseCategory(
    @StringRes override val nameResId: Int,
    @DrawableRes override val iconResId: Int
): Category {
    FOOD(R.string.food, R.drawable.ic_food),
    TRANSPORTATION(R.string.transportation, R.drawable.ic_transportation),
    CAR(R.string.car, R.drawable.ic_car),
    ENTERTAINMENT(R.string.entertainment, R.drawable.ic_entertainment),
    SHOPPING(R.string.shopping, R.drawable.ic_shopping),
    TELEPHONE(R.string.telephone, R.drawable.ic_telephone)
}

enum class IncomeCategory(
    @StringRes override val nameResId: Int,
    @DrawableRes override val iconResId: Int
): Category {
    SALARY(R.string.salary, R.drawable.ic_salary),
    AWARD(R.string.award, R.drawable.ic_award)
}