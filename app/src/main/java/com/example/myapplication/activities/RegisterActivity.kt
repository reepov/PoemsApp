package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.myapplication.R
import okhttp3.*
import okhttp3.internal.EMPTY_REQUEST
import java.io.IOException

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)
        val nickname = findViewById<EditText>(R.id.enterNickname)
        val login = findViewById<EditText>(R.id.enterLogin)
        val password = findViewById<EditText>(R.id.enterPassword)
        val secondPass = findViewById<EditText>(R.id.enterPasswordSecond)
        val register = findViewById<Button>(R.id.signIn)
        val enteredCode = findViewById<EditText>(R.id.enterCode)
        val sendCode = findViewById<Button>(R.id.sendCode)
        val signed = findViewById<TextView>(R.id.textIfSignedIn)
        sendCode.isVisible = false
        enteredCode.isVisible = false
        signed.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
        register.setOnClickListener {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            if (secondPass.text.toString() != password.text.toString()) Toast.makeText(
                applicationContext,
                "Пароли не совпадают",
                Toast.LENGTH_SHORT
            ).show()
            if (!login.text.contains("@") || login.text.indexOf("@") == 0 || login.text.indexOf("@") == login.text.length - 1 ) Toast.makeText(
                applicationContext,
                "Введен некорректный адрес электронной почты",
                Toast.LENGTH_SHORT
            ).show()
            else if (password.text.isEmpty()) Toast.makeText(applicationContext, "Введите пароль", Toast.LENGTH_SHORT).show()
            else
            {
                var code = ""
                var flag = false
                var url = "http://185.119.56.91/api/User/RegisterMobile?email=${login.text}"
                val client = OkHttpClient()
                var request = Request.Builder()
                    .url(url)
                    .post(EMPTY_REQUEST)
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {

                    }
                    override fun onResponse(call: Call, response: Response) {
                        code = response.body?.string().toString()
                        flag  = true
                    }
                })
                while(!flag) Thread.sleep(100)
                flag = false
                println(code)
                if (code == "exists") Toast.makeText(applicationContext, "Пользователь с данным email уже существует", Toast.LENGTH_SHORT).show()
                else {
                    enteredCode.isVisible = true
                    sendCode.isVisible = true
                    nickname.isVisible = false
                    login.isVisible = false
                    password.isVisible = false
                    secondPass.isVisible = false
                    register.isVisible = false
                    signed.isVisible = false
                    sendCode.setOnClickListener {
                        println(code)
                        if (enteredCode.text.toString() == code) {
                            url =
                                "http://185.119.56.91/api/User/EndRegisterMobile?email=${login.text}&nickname=${nickname.text}&password=${password.text}"
                            request = Request.Builder()
                                .url(url)
                                .post(EMPTY_REQUEST)
                                .build()
                            client.newCall(request).enqueue(object : Callback {
                                override fun onFailure(call: Call, e: IOException) {

                                }
                                override fun onResponse(call: Call, response: Response) {
                                    val intent =
                                        Intent(applicationContext, LoginActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                    startActivity(intent)
                                    finishAffinity()
                                }
                            })
                        } else {
                            enteredCode.setText("")
                            Toast.makeText(
                                applicationContext,
                                "Проверочный код не совпадает",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

        }
    }
}