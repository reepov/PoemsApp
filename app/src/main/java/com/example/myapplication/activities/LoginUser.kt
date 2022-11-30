package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import okhttp3.*
import java.io.IOException


class LoginUser : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var spEditor: SharedPreferences.Editor
    lateinit var login: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)
        sharedPreferences = getSharedPreferences("USER_INFO_SP", Context.MODE_PRIVATE)
        val reg = findViewById<TextView>(R.id.textIfNotRegister)
        login = findViewById(R.id.enterLogIn)
        val password = findViewById<EditText>(R.id.enterPassWord)
        val logIn = findViewById<Button>(R.id.logIn)
        login.setText("+7(")
        val maxLengthOfEditText = 16
        login.filters = arrayOf<InputFilter>(LengthFilter(maxLengthOfEditText))
        login.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(it: CharSequence?, start: Int, before: Int, count: Int) {

                if(start == 13 && before == 1 && count == 0)
                {
                    println(login.text)
                    login.setText(login.text.substring(0, login.text.length - 1))
                    println(login.text)
                }
                else if(start == 10 && before == 1 && count == 0)
                {
                    println(login.text)
                    login.setText(login.text.substring(0, login.text.length - 1))
                    println(login.text)
                }
                else if(start == 6 && before == 1 && count == 0)
                {
                    println(login.text)
                    login.setText(login.text.substring(0, login.text.length - 1))
                    println(login.text)
                }
                when(login.text.length)
                {
                    0, 1, 2 -> login.setText("+7(")
                    6 -> login.setText("$it)")
                    10 -> login.setText("$it-")
                    13 -> login.setText("$it-")
                    else -> {}
                }
                login.setSelection(login.length())
            }

            //+7(926)042-05-45
            override fun afterTextChanged(it: Editable) {

            }
        })

        reg.setOnClickListener {
            val intent = Intent(this, RegisterUser::class.java)
            startActivity(intent)
            finish()
        }
        logIn.setOnClickListener {
            if(login.text.length == 16 && password.text.isNotEmpty())
            {
                val client = OkHttpClient()
                var bool = true
                var userId : String
                val request = Request.Builder()
                    .url("http://185.119.56.91/api/User/LoginMobile?email=${login.text}&password=${password.text}")
                    .build()
                client.newCall(request).enqueue(object: Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        println("error")
                    }
                    override fun onResponse(call: Call, response: Response) {
                        userId = response.body?.string().toString()
                        if(response.code == 200)
                        {
                            bool = false
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
                    }
                })
                while(bool){
                    Thread.sleep(100)
                    continue
                }
                bool = true
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
            }
            else Toast.makeText(applicationContext, "Невалидный номер телефона и/или не введен пароль", Toast.LENGTH_SHORT).show()
        }
    }
}