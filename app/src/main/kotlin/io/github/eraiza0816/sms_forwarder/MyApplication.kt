package io.github.eraiza0816.sms_forwarder

import android.app.Application
import android.content.Context
import java.io.PrintWriter
import java.io.StringWriter

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            logCrash(this, throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun logCrash(context: Context, e: Throwable) {
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        val exceptionAsString = sw.toString()
        writeLog(context, "\n\n--- GLOBAL CRASH DETECTED ---\n$exceptionAsString\n--- END CRASH REPORT ---\n")
    }
}