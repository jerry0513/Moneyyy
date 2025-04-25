package com.moneyyy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.moneyyy.ui.transaction.TransactionRoute
import com.moneyyy.ui.transaction.TransactionScreenViewModel
import com.moneyyy.ui.transactionedit.TransactionEditRoute
import com.moneyyy.ui.transactionedit.TransactionEditViewModel
import com.moneyyy.ui.transactions.TransactionsRoute

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Transactions.route
    ) {
        addTransactionsScreen(navController)
        addTransactionScreen(navController)
        addTransactionAddScreen(navController)
        addTransactionEditScreen(navController)
    }
}

private fun NavGraphBuilder.addTransactionsScreen(navController: NavController) {
    composable(Screen.Transactions.route) {
        TransactionsRoute(
            viewModel = hiltViewModel(),
            onAddClick = { navController.navigate(Screen.TransactionAdd.route) },
            onItemClick = { navController.navigate(Screen.Transaction.createRoute(it)) }
        )
    }
}

private fun NavGraphBuilder.addTransactionScreen(navController: NavHostController) {
    composable(
        route = Screen.Transaction.route,
        arguments = listOf(
            navArgument(ARGUMENT_ID) {
                type = NavType.IntType
            }
        )
    ) { backStackEntry ->
        val arguments = backStackEntry.requireArguments()
        val id = arguments.getInt(ARGUMENT_ID)
        val viewModel =
            hiltViewModel<TransactionScreenViewModel, TransactionScreenViewModel.Factory> {
                it.create(id)
            }
        TransactionRoute(
            viewModel = viewModel,
            onNavigationIconClick = { navController.popBackStack() },
            onDeleted = { navController.popBackStack() },
            onEditClick = { navController.navigate(Screen.TransactionEdit.createRoute(id)) }
        )
    }
}

private fun NavGraphBuilder.addTransactionAddScreen(navController: NavHostController) {
    composable(Screen.TransactionAdd.route) {
        val viewModel = hiltViewModel<TransactionEditViewModel, TransactionEditViewModel.Factory> {
            it.create(null)
        }
        TransactionEditRoute(
            viewModel = viewModel,
            onNavigationIconClick = { navController.popBackStack() },
            onEditSuccess = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.addTransactionEditScreen(navController: NavHostController) {
    composable(
        route = Screen.TransactionEdit.route,
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
            onNavigationIconClick = { navController.popBackStack() },
            onEditSuccess = { navController.popBackStack() }
        )
    }
}

fun NavBackStackEntry.requireArguments() = requireNotNull(arguments)