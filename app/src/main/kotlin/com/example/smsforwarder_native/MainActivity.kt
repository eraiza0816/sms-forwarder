package com.example.smsforwarder_native

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.smsforwarder_native.databinding.ActivityMainBinding
import java.io.File

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

        binding.buttonSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        requestSmsPermission()
    }

    override fun onResume() {
        super.onResume()
        refreshLog()
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

