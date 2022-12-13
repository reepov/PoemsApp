package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import okhttp3.*
import okhttp3.internal.EMPTY_REQUEST
import java.io.IOException

class RegisterUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)
        val nickname = findViewById<EditText>(R.id.enterNickname)
        val login = findViewById<EditText>(R.id.enterLogin)
        val password = findViewById<EditText>(R.id.enterPassword)
        val secondPass = findViewById<EditText>(R.id.enterPasswordSecond)
        val register = findViewById<Button>(R.id.signIn)
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
                val url = "http://185.119.56.91/api/User/RegisterMobile?email=${login.text}&nickname=${nickname.text}&password=${password.text}"
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(url)
                    .post(EMPTY_REQUEST)
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {

                    }
                    override fun onResponse(call: Call, response: Response) {
                        val intent = Intent(applicationContext, LoginUser::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        startActivity(intent)
                        finish()
                    }
                })
            }

        }
    }
}