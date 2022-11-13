package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.*
import okhttp3.internal.EMPTY_REQUEST
import java.io.IOException

class LoginUser : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var spEditor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)
        sharedPreferences = getSharedPreferences("USER_INFO_SP", Context.MODE_PRIVATE)
        var reg = findViewById<TextView>(R.id.textIfNotRegister)
        var login = findViewById<EditText>(R.id.enterLogIn)
        var password = findViewById<EditText>(R.id.enterPassWord)
        var logIn = findViewById<Button>(R.id.logIn)
        reg.setOnClickListener {
            val intent = Intent(this, RegisterUser::class.java)
            startActivity(intent)
            finish()
        }
        logIn.setOnClickListener {
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
                    userId = response?.body?.string().toString()
                    println(response.code.toString() + " " + userId + "  dsjkahslfkgjhdflkjghldkfjhglkdjfhglksdfjhg")
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
            startActivity(intent)
            finish()
        }
    }

}