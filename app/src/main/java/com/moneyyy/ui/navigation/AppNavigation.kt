package com.moneyyy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.moneyyy.data.RecordRepository
import com.moneyyy.ui.record.RecordRoute
import com.moneyyy.ui.recordedit.RecordEditViewModel
import com.moneyyy.ui.record.RecordScreenViewModel
import com.moneyyy.ui.recordedit.RecordEditRoute
import com.moneyyy.ui.records.RecordsRoute
import com.moneyyy.ui.records.RecordsScreenViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Records.route
    ) {
        addRecordsScreen(navController)
        addRecordScreen(navController)
        addRecordAddScreen(navController)
        addRecordEditScreen(navController)
    }
}

private fun NavGraphBuilder.addRecordsScreen(navController: NavController) {
    composable(Screen.Records.route) {
        val viewModel: RecordsScreenViewModel = viewModel(
            factory = RecordsScreenViewModel.provideFactory(RecordRepository())
        )
        RecordsRoute(
            viewModel = viewModel,
            onAddRecordClick = { navController.navigate(Screen.RecordAdd.route) },
            onRecordClick = { navController.navigate(Screen.Record.createRoute(it)) }
        )
    }
}

private fun NavGraphBuilder.addRecordScreen(navController: NavHostController) {
    composable(
        route = Screen.Record.route,
        arguments = listOf(
            navArgument(ARGUMENT_ID) {
                type = NavType.IntType
            }
        )
    ) { backStackEntry ->
        val arguments = backStackEntry.requireArguments()
        val id = arguments.getInt(ARGUMENT_ID)
        val viewModel: RecordScreenViewModel = viewModel(
            factory = RecordScreenViewModel.provideFactory(
                id = id,
                recordRepository = RecordRepository()
            )
        )
        RecordRoute(
            viewModel = viewModel,
            onNavigationIconClicked = { navController.popBackStack() },
            onDeleted = { navController.popBackStack() },
            onEditClicked = { navController.navigate(Screen.RecordEdit.createRoute(id)) }
        )
    }
}

private fun NavGraphBuilder.addRecordAddScreen(navController: NavHostController) {
    composable(Screen.RecordAdd.route) {
        val viewModel: RecordEditViewModel = viewModel(
            factory = RecordEditViewModel.provideFactory(
                id = null,
                recordRepository = RecordRepository()
            )
        )
        RecordEditRoute(
            viewModel = viewModel,
            onNavigationIconClicked = { navController.popBackStack() },
            onEditingSuccessful = { navController.popBackStack() }
        )
    }
}

private fun NavGraphBuilder.addRecordEditScreen(navController: NavHostController) {
    composable(
        route = Screen.RecordEdit.route,
        arguments = listOf(
            navArgument(ARGUMENT_ID) {
                type = NavType.IntType
            }
        )
    ) { backStackEntry ->
        val arguments = backStackEntry.requireArguments()
        val viewModel: RecordEditViewModel = viewModel(
            factory = RecordEditViewModel.provideFactory(
                id = arguments.getInt(ARGUMENT_ID),
                recordRepository = RecordRepository()
            )
        )
        RecordEditRoute(
            viewModel = viewModel,
            onNavigationIconClicked = { navController.popBackStack() },
            onEditingSuccessful = { navController.popBackStack() }
        )
    }
}

fun NavBackStackEntry.requireArguments() = requireNotNull(arguments)