package com.example.workerpractice

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import com.example.workerpractice.worker.GetTodoWorker
import java.time.Duration
import java.util.concurrent.TimeUnit

class MainViewModel : ViewModel() {
    val worker = OneTimeWorkRequestBuilder<GetTodoWorker>()
        .apply {
//            setBackoffCriteria(
//                BackoffPolicy.EXPONENTIAL,
//                200,
//                TimeUnit.MILLISECONDS
//            )
        }.build()
}