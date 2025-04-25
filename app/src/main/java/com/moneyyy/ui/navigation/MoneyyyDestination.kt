package com.moneyyy.ui.navigation

sealed class MoneyyyDestination(val route: String) {
    data object Transactions : MoneyyyDestination("transactions")

    data object Transaction : MoneyyyDestination("transaction/{$ARGUMENT_ID}") {
        fun createRoute(id: Int) = "transaction/$id"
    }

    data object TransactionEdit : MoneyyyDestination("transaction/edit/{$ARGUMENT_ID}") {
        fun createRoute(id: Int) = "transaction/edit/$id"
    }

    data object TransactionAdd : MoneyyyDestination("transaction/edit")

    data object Statistics : MoneyyyDestination("statistics/{$ARGUMENT_YEAR}/{$ARGUMENT_MONTH}") {
        fun createRoute(year: Int, month: Int) = "statistics/$year/$month"
    }
}

const val ARGUMENT_ID = "id"
const val ARGUMENT_YEAR = "year"
const val ARGUMENT_MONTH = "month"