package com.moneyyy.ui.navigation

sealed class Screen(val route: String) {
    data object Transactions: Screen("transactions")
    data object Transaction: Screen("transaction/{$ARGUMENT_ID}") {
        fun createRoute(id: Int) = "transaction/$id"
    }
    data object TransactionEdit: Screen("transaction/edit/{$ARGUMENT_ID}") {
        fun createRoute(id: Int) = "transaction/edit/$id"
    }
    data object TransactionAdd: Screen("transaction/edit")
}

const val ARGUMENT_ID = "id"