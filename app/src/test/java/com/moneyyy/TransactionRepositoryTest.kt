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
import java.time.LocalDate
import java.time.ZoneId

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

        coVerify(exactly = 1) { dao.add(transaction) }
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

        coVerify(exactly = 1) { dao.update(transaction) }
    }

    @Test
    fun `deleteTransaction calls DAO delete`() = runTest {
        val id = 1

        repository.deleteTransaction(id)

        coVerify(exactly = 1) { dao.delete(id) }
    }

    @Test
    fun `observeTransaction returns correct transaction`() = runTest {
        repository.observeTransaction(1)

        verify(exactly = 1) { dao.get(1) }
    }

    @Test
    fun `observeTransactionsByYearMonth calls DAO with correct time range`() = runTest {
        val start = LocalDate.of(2025, 5, 1)
            .atStartOfDay(ZoneId.systemDefault())
        val expectedStart = start.toInstant().toEpochMilli()
        val expectedEnd = start.plusMonths(1).toInstant().toEpochMilli()

        repository.observeTransactionsByYearMonth(2025, 5)

        verify(exactly = 1) { dao.getAllByYearMonth(expectedStart, expectedEnd) }
    }
}