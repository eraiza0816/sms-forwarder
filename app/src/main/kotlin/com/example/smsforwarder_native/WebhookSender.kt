package com.example.smsforwarder_native

import android.content.Context
import java.io.File
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import org.json.JSONObject
import java.util.Date
import java.util.Locale

object WebhookSender {

    suspend fun send(context: Context, urlString: String, sender: String, body: String) {
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")

            val jsonPayload = JSONObject()
            jsonPayload.put("content", "送信元: $sender\n内容: $body")

            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(jsonPayload.toString())
            writer.flush()

            val responseCode = connection.responseCode
            if (responseCode >= 400) {
                val errorStream = connection.errorStream?.bufferedReader()?.readText()
                writeLog(context, "転送失敗: $sender (HTTP $responseCode)\nサーバーの応答: $errorStream")
            } else {
                writeLog(context, "転送成功: $sender (HTTP $responseCode)")
            }

        } catch (e: Exception) {
            writeLog(context, "転送失敗: ${e.message}")
        }
    }
}
