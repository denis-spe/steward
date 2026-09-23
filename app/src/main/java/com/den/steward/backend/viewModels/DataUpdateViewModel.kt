// Glory be to the name of the LORD GOD of host and to LORD JESUS CHRIST
package com.den.steward.backend.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.den.steward.backend.entitles.PlanStatus
import com.den.steward.backend.entitles.Transaction
import com.den.steward.backend.useCase.UpdateDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataUpdateViewModel @Inject constructor(
    private val updateDataUseCase: UpdateDateUseCase
) : ViewModel() {

    /**
     * Updates the fulfillment status of a transaction.
     * @param transactionId The ID of the transaction.
     * @param planStatus The new fulfillment status.
     * @param fulfillment The updated fulfillment object.
     */
    fun updatePlanFulfillmentStatus(
        transactionId: String,
        planStatus: PlanStatus,
        fulfillment: Transaction.PlanFulfillment,
    ) {
        viewModelScope.launch {
            updateDataUseCase.updateTransactionFulfillment(
                transactionId,
                fulfillment.id,
                fulfillment.copy(
                    status = planStatus
                )
            )
        }
    }
}