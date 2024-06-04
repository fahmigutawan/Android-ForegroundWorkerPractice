package com.example.workerpractice.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.workerpractice.R
import com.example.workerpractice.model.SingleTodoResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class GetTodoWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            gson()
        }
    }
    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    override fun doWork(): Result {
        //Jika versi Android Oreo++, maka diperlukan untuk membuat notification channel terlebih dahulu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel("0123")
        }

        //Notification
        val notification = NotificationCompat.Builder(applicationContext, "0123")
            .setContentTitle("FOREGROUND")
            .setContentText("Some Important Job")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .build()

        //Kemudian dibuat ForegroundInfo, ini adalah fungsi yang sudah ada dari worker
        val foregroundInfo = ForegroundInfo(0, notification)

        //Kemudian buat foregroundAsync, jadi kita tidak perlu menampilkan foreground & notification secara manual
        setForegroundAsync(foregroundInfo)

        return runBlocking {
            Log.e("DOWORK", "CALLED")

            //Cek apakah ada internet, jika tidak ada maka worker tidak akan berhenti.
            //Dapat dicek pada Log, worker akan selalu bekerja sampai internet menyala
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            while (
                (connectivityManager
                    .activeNetworkInfo
                    ?.isConnectedOrConnecting == false
                        || connectivityManager.activeNetworkInfo == null)
            ) {
                delay(5000)
                Log.e("INTERNET OFF", "RETRYING")
            }

            try {
                val res = client.get("https://jsonplaceholder.typicode.com/todos")

                if (res.status == HttpStatusCode.OK) {
                    val body = res.body<List<SingleTodoResponse>>()
                    Log.e("BERHASIL", body.toString())
                    Result.success()
                } else {
                    Log.e("API CALL FAILED", "RETRYING")
                    Result.retry()
                }
            } catch (e: Exception) {
                Log.e("ERROR", e.message.toString())
                Result.failure()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(channelId: String) {
        val channel = NotificationChannel(channelId, "CHANNEL", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
    }
}