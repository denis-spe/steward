package com.den.steward.backend.viewModels

import android.content.Context
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.GoalStatus
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.states.DataState
import com.den.steward.backend.states.goalState.GoalDataState
import com.den.steward.backend.states.goalState.GoalMainSummary
import com.den.steward.backend.states.goalState.GoalScreenUiState
import com.den.steward.backend.useCase.DataDeletionUseCase
import com.den.steward.backend.useCase.DataFetchUseCase
import com.den.steward.backend.useCase.UpdateTransactionUseCase
import com.den.steward.helper.formattedDate
import com.den.steward.helper.toLocalDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.debounce
import java.time.LocalDate
import javax.inject.Inject

private data class GoalScreenController(
    val transactions: DataState<List<Transaction>>,
    val search: String
)

@HiltViewModel
class GoalScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val deletionUseCase: DataDeletionUseCase,
    val updateTransaction: UpdateTransactionUseCase,
    dataFetchUseCase: DataFetchUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "TransactionScreenVM"
    }

    private val _goalScreenUiState = MutableStateFlow(GoalScreenUiState())
    val goalScreenUiState = _goalScreenUiState.asStateFlow()

    val dataUpdateState = updateTransaction.dataUpdateState

    @OptIn(FlowPreview::class)
    private val debouncedSearchQueryFlow =
        snapshotFlow { _goalScreenUiState.value.searchTextState.text }
            .map { it.toString().trim() } // Convert CharSequence to String and trim spaces
            .distinctUntilChanged()       // Don't re-trigger if text variations match
            .debounce(java.time.Duration.ofMillis(300))

    val goalTransactionDataState =
        combine(
            dataFetchUseCase.fetchAllTransactions,
            debouncedSearchQueryFlow
        ) { transactions, search ->
            GoalScreenController(
                transactions,
                search
            )
        }.map { transactionScreenController ->
            val (transactions, search) = transactionScreenController

            when (transactions) {
                is DataState.Success -> {
                    val transactions: List<Transaction.Goal> =
                        transactions.data.filterIsInstance<Transaction.Goal>()
                            .filter { transaction ->
                                val typeLabel = context.getString(transaction.type.label)

                                transaction.getLabel.contains(search, ignoreCase = true) ||
                                        transaction.getNote.contains(search, ignoreCase = true) ||
                                        transaction.getStatus?.contains(
                                            search,
                                            ignoreCase = true
                                        ) ?: false ||
                                        transaction.getAffectedAmount?.label?.contains(
                                            search,
                                            ignoreCase = true
                                        ) ?: false ||
                                        transaction.getPaymentMethodOrNull?.label?.contains(
                                            search,
                                            ignoreCase = true
                                        ) ?: false ||
                                        typeLabel.contains(search, ignoreCase = true)
                            }

                    val now = LocalDate.now()

                    val totalCompletedAchievement = transactions.sumOf {
                        it.achievement.filter { achieve -> achieve.status == GoalStatus.COMPLETED }
                            .size
                    }

                    val totalFailedAchievement = transactions.sumOf {
                        it.achievement.filter { achieve -> achieve.status == GoalStatus.FAILED }
                            .size
                    }

                    val totalAchievements = totalCompletedAchievement + totalFailedAchievement
                    val totalCompletedAchievementPercent = if (totalAchievements > 0) {
                        ((totalCompletedAchievement.toDouble() / totalAchievements) * 100).toInt()
                    } else 0
                    val totalFailedAchievementPercent = if (totalAchievements > 0) {
                        ((totalFailedAchievement.toDouble() / totalAchievements) * 100).toInt()
                    } else 0

                    val totalCurrentValue = transactions.sumOf {
                        it.attain.sumOf { attain -> attain.value }
                    }

                    val totalTarget = transactions.sumOf {
                        it.value
                    }

                    val goalMainSummary = GoalMainSummary(
                        rate = if (totalTarget > 0.0) (totalCurrentValue / totalTarget) * 100 else 0.0,
                        totalCompletedAchievement = totalCompletedAchievement,
                        totalFailedAchievement = totalFailedAchievement,
                        totalCompletedAchievementPercent = totalCompletedAchievementPercent,
                        totalFailedAchievementPercent = totalFailedAchievementPercent
                    )

                    val transactionDataState = GoalDataState(
                        goalTransactions = transactions.groupBy {
                            val createdAt = it.createdAt.toLocalDateTime()
                            when {
                                now == createdAt.toLocalDate() -> "Today"
                                now.minusDays(1) == createdAt.toLocalDate() -> "Yesterday"
                                else -> createdAt.formattedDate
                            }
                        }.toImmutableMap(),
                        goalMainSummary = goalMainSummary
                    )
                    DataState.Success(transactionDataState)
                }

                is DataState.Error -> DataState.Error(transactions.message)
                is DataState.Loading -> DataState.Loading
            }
        }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = DataState.Loading
            )

    // ===================== Delete =====================
    fun deleteTransaction(transactionToBeDeleted: SnapshotStateList<Transaction>? = null) {
        viewModelScope.launch {
            val transaction =
                _goalScreenUiState.value.selectedTransactionToDelete ?: return@launch
            runCatching {
                deletionUseCase.deleteTransaction(transaction)
            }.onSuccess {
                transactionToBeDeleted?.removeIf { it.id == transaction.id }
            }
        }

        // Reset the state
        updateSelectedTransactionToDelete(null)
        updateOpenDeleteDialog(false)
    }

    fun updateSelectedTransactionToDelete(transaction: Transaction?) {
        _goalScreenUiState.update {
            it.copy(
                selectedTransactionToDelete = transaction,
                openDeleteDialog = transaction != null
            )
        }
    }

    fun updateOpenDeleteDialog(show: Boolean) {
        _goalScreenUiState.update {
            it.copy(openDeleteDialog = show)
        }
    }

    fun updateOpenDeleteAllDialog(show: Boolean) {
        _goalScreenUiState.update {
            it.copy(openDeleteAllDialog = show)
        }
    }

    fun deleteAllTransactions(transactionToBeDeleted: SnapshotStateList<Transaction>) {
        val itemsToDelete = transactionToBeDeleted.toList().map { it.id }
        updateOpenDeleteAllDialog(false)
        transactionToBeDeleted.clear()

        viewModelScope.launch(Dispatchers.IO) {
            deletionUseCase.deletingMultipleTransaction(
                transactionIds = itemsToDelete
            )
        }
    }


    // ===================== Update Transaction =========================
    fun onUpdateTransaction() {
        viewModelScope.launch {
            if (updateTransaction.updateTransaction()) {
                updateTransaction.onReset()
            }
        }
    }
}