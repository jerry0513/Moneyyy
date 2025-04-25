package com.moneyyy

import com.moneyyy.data.database.TransactionDao
import com.moneyyy.data.database.TransactionEntity
import com.moneyyy.data.model.CategoryType
import com.moneyyy.data.repository.DefaultTransactionRepository
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DefaultTransactionRepositoryTest {
    private lateinit var dao: TransactionDao
    private lateinit var repository: DefaultTransactionRepository

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = DefaultTransactionRepository(dao)
    }

    @Test
    fun `addTransaction delegates to DAO`() = runTest {
        val transaction = TransactionEntity(
            amount = 100,
            categoryKey = "gift",
            categoryType = CategoryType.EXPENSE,
            note = "Birthday",
            timestamp = 1_716_000_000_000L
        )

        repository.addTransaction(transaction)

        coVerify { dao.add(transaction) }
    }

    @Test
    fun `updateTransaction delegates to DAO`() = runTest {
        val transaction = TransactionEntity(
            id = 5,
            amount = 300,
            categoryKey = "food",
            categoryType = CategoryType.EXPENSE,
            note = "Lunch",
            timestamp = 1_716_000_000_000L
        )

        repository.updateTransaction(transaction)

        coVerify { dao.update(transaction) }
    }

    @Test
    fun `deleteTransaction calls DAO delete`() = runTest {
        val id = 1

        repository.deleteTransaction(id)

        coVerify { dao.delete(id) }
    }

    @Test
    fun `observeTransaction returns correct transaction`() = runTest {
        repository.observeTransaction(1)

        verify { dao.get(1) }
    }

    @Test
    fun `observeTransactionsByYearMonth calls DAO with correct time range`() = runTest {
        repository.observeTransactionsByYearMonth(2025, 5)

        verify { dao.getAllByYearMonth(1746028800000, 1748707200000) }
    }
}