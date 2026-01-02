package com.example.smsforwarder_native

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smsforwarder_native.databinding.ActivitySettingsBinding
import java.io.File

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 戻るボタンを表示

        val prefs = getSharedPreferences("sms_forwarder_prefs", Context.MODE_PRIVATE)

        binding.edittextWebhookUrl.setText(prefs.getString("webhook_url", ""))

        binding.buttonSaveWebhook.setOnClickListener {
            prefs.edit().putString("webhook_url", binding.edittextWebhookUrl.text.toString()).apply()
        }

        binding.buttonRefreshLogSettings.setOnClickListener {
            refreshLog()
        }

        binding.buttonClearLogSettings.setOnClickListener {
            val logFile = File(getExternalFilesDir(null), "sms_forwarder.log")
            if (logFile.exists()) {
                logFile.delete()
            }
            refreshLog()
        }

        refreshLog()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun refreshLog() {
        val logFile = File(getExternalFilesDir(null), "sms_forwarder.log")
        if (logFile.exists()) {
            binding.textviewLogSettings.text = logFile.readText()
        } else {
            binding.textviewLogSettings.text = "ログファイルがまだありません。"
        }
    }
}
