package com.moneyyy.ui.navigation

sealed class Screen(val route: String) {
    data object Records: Screen("records")
    data object Record: Screen("record/{$ARGUMENT_ID}") {
        fun createRoute(id: Int) = "record/$id"
    }
    data object RecordEdit: Screen("record/edit/{$ARGUMENT_ID}") {
        fun createRoute(id: Int) = "record/edit/$id"
    }
    data object RecordAdd: Screen("record/edit")
}

const val ARGUMENT_ID = "id"