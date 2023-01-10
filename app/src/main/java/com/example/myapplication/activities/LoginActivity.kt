package com.example.myapplication.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.myapplication.R
import com.example.myapplication.services.APISender

class LoginActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var spEditor: SharedPreferences.Editor
    lateinit var login: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        sharedPreferences = getSharedPreferences("USER_INFO_SP", Context.MODE_PRIVATE)
        val reg = findViewById<TextView>(R.id.textIfNotRegister)
        val forget = findViewById<TextView>(R.id.textIfForgetPassword)
        val api = APISender()
        login = findViewById(R.id.enterLogIn)
        val password = findViewById<EditText>(R.id.enterPassWord)
        val logIn = findViewById<Button>(R.id.logIn)
        forget.setOnClickListener {
            val intent = Intent(this, ForgetActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
        reg.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
        logIn.setOnClickListener {
            if(password.text.isNotEmpty())
            {
                val founded : Boolean
                val userId = api.get("http://185.119.56.91/api/User/LoginMobile?email=${login.text}&password=${password.text}")
                if(userId != "false")
                {
                    founded = true
                    spEditor = sharedPreferences.edit()
                    spEditor.putString("Login", login.text.toString())
                    println(login.text.toString())
                    spEditor.putString("Password", password.text.toString())
                    println(password.text.toString())
                    spEditor.putString("CurrentUserId", userId)
                    println(userId)
                    spEditor.putBoolean("isRemembered", true)
                    spEditor.apply()
                }
                else
                {
                    founded = false
                }
                if(founded){
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    finishAffinity()
                }
                else Toast.makeText(applicationContext, "Пользователь не найден", Toast.LENGTH_SHORT).show()
            }
            else Toast.makeText(applicationContext, "Невалидный номер телефона и/или не введен пароль", Toast.LENGTH_SHORT).show()
        }
    }
}