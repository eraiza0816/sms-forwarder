package io.github.eraiza0816.sms_forwarder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.net.UnknownHostException

class RetryWebhookWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val url = inputData.getString("url") ?: return Result.failure()
        val sender = inputData.getString("sender") ?: return Result.failure()
        val body = inputData.getString("body") ?: return Result.failure()

        return try {
            val success = WebhookSender.send(applicationContext, url, sender, body)
            if (success) {
                writeLog(applicationContext, "転送成功（リトライ）: $sender")
                Result.success()
            } else {
                writeLog(applicationContext, "転送失敗（リトライ）: $sender")
                Result.retry()
            }
        } catch (e: UnknownHostException) {
            writeLog(applicationContext, "ホスト解決エラー（リトライ）: ${e.message}")
            // Specifically catch host resolution errors to retry
            Result.retry()
        } catch (e: Exception) {
            writeLog(applicationContext, "予期せぬエラー（リトライ）: ${e.message}")
            // For other errors, fail the work
            Result.failure()
        }
    }
}
