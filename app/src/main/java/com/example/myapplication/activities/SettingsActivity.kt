package com.example.myapplication.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class SettingsActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_layout)
        sharedPreferences = getSharedPreferences("USER_INFO_SP", Context.MODE_PRIVATE)
        val leave = findViewById<Button>(R.id.leaveButton)
        leave.setOnClickListener {
            val builder1: AlertDialog.Builder = AlertDialog.Builder(this)
            builder1.setMessage("Вы хотите выйти из аккаунта?")
            builder1.setCancelable(true)
            builder1.setPositiveButton("Да") { dialog, _ ->
                sharedPreferences = getSharedPreferences("USER_INFO_SP", Context.MODE_PRIVATE)
                sharedPreferences.edit()
                    .remove("CurrentUserId")
                    .remove("Password")
                    .remove("Login")
                    .remove("isRemembered")
                    .apply()
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                dialog.cancel()
                startActivity(intent)
                finishAffinity()
                overridePendingTransition(0, 0)
            }
            builder1.setNegativeButton("Нет") { dialog, _ -> dialog.cancel() }
            val alert11: AlertDialog = builder1.create()
            alert11.show()
        }
    }
}