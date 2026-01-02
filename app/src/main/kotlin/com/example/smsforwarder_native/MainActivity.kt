package com.example.smsforwarder_native

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.smsforwarder_native.databinding.ActivityMainBinding
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            writeLog(this, "Permission ${Manifest.permission.RECEIVE_SMS} granted: $isGranted")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        writeLog(this, "MainActivity.onCreate started")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("sms_forwarder_prefs", Context.MODE_PRIVATE)

        binding.edittextWebhookUrl.setText(prefs.getString("webhook_url", ""))

        binding.buttonSave.setOnClickListener {
            prefs.edit().putString("webhook_url", binding.edittextWebhookUrl.text.toString()).apply()
        }

        binding.buttonRefreshLog.setOnClickListener {
            refreshLog()
        }

        binding.buttonClearLog.setOnClickListener {
            val logFile = File(getExternalFilesDir(null), "sms_forwarder.log")
            if (logFile.exists()) {
                logFile.delete()
            }
            refreshLog()
        }

        refreshLog()
        requestSmsPermission()
    }

    private fun requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECEIVE_SMS)
        }
    }

    private fun refreshLog() {
        val logFile = File(getExternalFilesDir(null), "sms_forwarder.log")
        if (logFile.exists()) {
            binding.textviewLog.text = logFile.readText()
        } else {
            binding.textviewLog.text = "ログファイルがまだありません。"
        }
    }
}

