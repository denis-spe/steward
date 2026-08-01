package com.den.steward.backend.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ExampleWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
//    workerDependency: WorkerDependency
) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        TODO("Not yet implemented")
    }
}