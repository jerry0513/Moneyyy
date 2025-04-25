package com.moneyyy

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.moneyyy.data.model.Category
import com.moneyyy.data.model.CategoryType
import com.moneyyy.data.model.ExpenseCategory
import com.moneyyy.ui.transactionedit.TransactionEditDate
import com.moneyyy.ui.transactionedit.TransactionEditScreen
import com.moneyyy.ui.transactionedit.TransactionEditUiState

import org.junit.runner.RunWith

import org.junit.Rule
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class TransactionEditScreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun keyboard_is_hidden_initially() {
        val uiState = TransactionEditUiState(
            isLoading = false,
            categoryType = CategoryType.EXPENSE,
            category = null,
            note = "",
            date = TransactionEditDate(
                year = 2025,
                month = 5,
                dayOfMonth = 1,
                timestamp = 1746057600000,
                correctedTimestamp = 1746057600000
            ),
            amountText = ""
        )

        composeTestRule.setContent {
            TransactionEditScreen(uiState = uiState)
        }

        composeTestRule
            .onNodeWithTag("transaction_edit_keyboard")
            .assertDoesNotExist()
    }

    @Test
    fun clicking_category_triggers_callback() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val expectedCategory = ExpenseCategory.entries.first()
        var clickedCategory: Category? = null

        val uiState = TransactionEditUiState(
            isLoading = false,
            categoryType = CategoryType.EXPENSE,
            category = null,
            note = "",
            date = TransactionEditDate(
                year = 2025,
                month = 5,
                dayOfMonth = 1,
                timestamp = 1746057600000,
                correctedTimestamp = 1746057600000
            ),
            amountText = ""
        )

        composeTestRule.setContent {
            TransactionEditScreen(
                uiState = uiState,
                onCategoryClick = { clickedCategory = it }
            )
        }

        composeTestRule
            .onNodeWithContentDescription(context.getString(expectedCategory.nameResId))
            .performClick()

        assert(clickedCategory == expectedCategory)
    }

    @Test
    fun keyboard_is_shown_after_category_click() {
        val uiState = TransactionEditUiState(
            isLoading = false,
            categoryType = CategoryType.EXPENSE,
            category = ExpenseCategory.entries.first(),
            note = "",
            date = TransactionEditDate(
                year = 2025,
                month = 5,
                dayOfMonth = 1,
                timestamp = 1746057600000,
                correctedTimestamp = 1746057600000
            ),
            amountText = ""
        )

        composeTestRule.setContent {
            TransactionEditScreen(uiState = uiState)
        }

        composeTestRule
            .onNodeWithTag("transaction_edit_keyboard")
            .assertIsDisplayed()
    }
}