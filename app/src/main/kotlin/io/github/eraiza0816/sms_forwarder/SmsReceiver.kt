package io.github.eraiza0816.sms_forwarder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle = intent.extras
            if (bundle != null) {
                val pdus = bundle.get("pdus") as Array<*>
                for (pdu in pdus) {
                    val sms = SmsMessage.createFromPdu(pdu as ByteArray)
                    val sender = sms.originatingAddress ?: "Unknown"
                    val body = sms.messageBody ?: ""

                    val prefs = context.getSharedPreferences("sms_forwarder_prefs", Context.MODE_PRIVATE)
                    val url = prefs.getString("webhook_url", null)

                    if (url != null && url.isNotEmpty()) {
                        val workRequest = OneTimeWorkRequestBuilder<RetryWebhookWorker>()
                            .setInputData(workDataOf(
                                "url" to url,
                                "sender" to sender,
                                "body" to body
                            ))
                            .setBackoffCriteria(
                                BackoffPolicy.LINEAR,
                                10,
                                TimeUnit.MINUTES
                            )
                            .build()
                        
                        WorkManager.getInstance(context).enqueue(workRequest)
                    }
                }
            }
        }
    }
}
