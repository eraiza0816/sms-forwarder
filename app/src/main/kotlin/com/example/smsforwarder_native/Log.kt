package com.example.smsforwarder_native

import android.content.Context
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun writeLog(context: Context, msg: String) {
    try {
        val dir = context.getExternalFilesDir(null)
        if (dir != null) {
            val file = File(dir, "sms_forwarder.log")
            val ts = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            file.appendText("[$ts] $msg\n")
        }
    } catch (e: Exception) {
        // Failsafe, do not crash the app
        e.printStackTrace()
    }
}
