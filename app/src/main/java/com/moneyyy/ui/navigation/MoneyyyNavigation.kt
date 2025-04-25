package com.moneyyy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.moneyyy.ui.statistics.StatisticsRoute
import com.moneyyy.ui.statistics.StatisticsViewModel
import com.moneyyy.ui.transaction.TransactionRoute
import com.moneyyy.ui.transaction.TransactionViewModel
import com.moneyyy.ui.transactionedit.TransactionEditRoute
import com.moneyyy.ui.transactionedit.TransactionEditViewModel
import com.moneyyy.ui.transactions.TransactionsRoute

@Composable
fun MoneyyyNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = MoneyyyDestination.Transactions.route
    ) {
        addTransactionsScreen(navController)
        addTransactionScreen(navController)
        addTransactionAddScreen(navController)
        addTransactionEditScreen(navController)
        addStatisticsScreen(navController)
    }
}

private fun NavGraphBuilder.addTransactionsScreen(navController: NavController) {
    composable(MoneyyyDestination.Transactions.route) {
        TransactionsRoute(
            viewModel = hiltViewModel(),
            onStatisticsClick = { year, month ->
                navController.navigate(MoneyyyDestination.Statistics.createRoute(year, month))
            },
            onAddClick = { navController.navigate(MoneyyyDestination.TransactionAdd.route) },
            onItemClick = { navController.navigate(MoneyyyDestination.Transaction.createRoute(it)) }
        )
    }
}

private fun NavGraphBuilder.addTransactionScreen(navController: NavHostController) {
    composable(
        route = MoneyyyDestination.Transaction.route,
        arguments = listOf(
            navArgument(ARGUMENT_ID) {
                type = NavType.IntType
            }
        )
    ) { backStackEntry ->
        val arguments = backStackEntry.requireArguments()
        val id = arguments.getInt(ARGUMENT_ID)
        val viewModel = hiltViewModel<TransactionViewModel, TransactionViewModel.Factory> {
            it.create(id)
        }
        TransactionRoute(
            viewModel = viewModel,
            onNavigationIconClick = { navController.popBackStackOrIgnore() },
            onDeleted = { navController.popBackStackOrIgnore() },
            onEditClick = { navController.navigate(MoneyyyDestination.TransactionEdit.createRoute(id)) }
        )
    }
}

private fun NavGraphBuilder.addTransactionAddScreen(navController: NavHostController) {
    composable(MoneyyyDestination.TransactionAdd.route) {
        val viewModel = hiltViewModel<TransactionEditViewModel, TransactionEditViewModel.Factory> {
            it.create(null)
        }
        TransactionEditRoute(
            viewModel = viewModel,
            onNavigationIconClick = { navController.popBackStackOrIgnore() },
            onEditSuccess = { navController.popBackStackOrIgnore() }
        )
    }
}

private fun NavGraphBuilder.addTransactionEditScreen(navController: NavHostController) {
    composable(
        route = MoneyyyDestination.TransactionEdit.route,
        arguments = listOf(
            navArgument(ARGUMENT_ID) {
                type = NavType.IntType
            }
        )
    ) { backStackEntry ->
        val arguments = backStackEntry.requireArguments()
        val id = arguments.getInt(ARGUMENT_ID)
        val viewModel = hiltViewModel<TransactionEditViewModel, TransactionEditViewModel.Factory> {
            it.create(id)
        }
        TransactionEditRoute(
            viewModel = viewModel,
            onNavigationIconClick = { navController.popBackStackOrIgnore() },
            onEditSuccess = { navController.popBackStackOrIgnore() }
        )
    }
}

private fun NavGraphBuilder.addStatisticsScreen(navController: NavHostController) {
    composable(
        route = MoneyyyDestination.Statistics.route,
        arguments = listOf(
            navArgument(ARGUMENT_YEAR) {
                type = NavType.IntType
            },
            navArgument(ARGUMENT_MONTH) {
                type = NavType.IntType
            }
        )
    ) { backStackEntry ->
        val arguments = backStackEntry.requireArguments()
        val year = arguments.getInt(ARGUMENT_YEAR)
        val month = arguments.getInt(ARGUMENT_MONTH)
        val viewModel = hiltViewModel<StatisticsViewModel, StatisticsViewModel.Factory> {
            it.create(year, month)
        }
        StatisticsRoute(
            viewModel = viewModel,
            onNavigationIconClick = { navController.popBackStackOrIgnore() }
        )
    }
}

fun NavBackStackEntry.requireArguments() = requireNotNull(arguments)

fun NavController.popBackStackOrIgnore() {
    if (currentBackStackEntry?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.RESUMED) == true) {
        popBackStack()
    }
}