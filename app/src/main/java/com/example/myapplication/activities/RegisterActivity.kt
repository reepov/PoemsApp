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
import com.example.myapplication.services.APISender
import okhttp3.*
import okhttp3.internal.EMPTY_REQUEST
import java.io.IOException
import java.util.regex.Pattern

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
        val codeResend = findViewById<TextView>(R.id.textIfResendCode)
        val api = APISender()
        sendCode.isVisible = false
        enteredCode.isVisible = false
        codeResend.isVisible = false
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
            else if (!login.text.contains("@") || login.text.indexOf("@") == 0 || login.text.indexOf("@") == login.text.length - 1 ) Toast.makeText(
                applicationContext,
                "Введен некорректный адрес электронной почты",
                Toast.LENGTH_SHORT
            ).show()
            else if (password.text.isEmpty()) Toast.makeText(applicationContext, "Введите пароль", Toast.LENGTH_SHORT).show()
            else if (!isValidPasswordFormat(password.text.toString())) Toast.makeText(applicationContext, "Пароль должен содержать минимум 8 цифр, одну заглавную букву, одну строчную букву, и содержать только символы латинского алфавита", Toast.LENGTH_LONG).show()
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
                    codeResend.isVisible = true
                    nickname.isVisible = false
                    login.isVisible = false
                    password.isVisible = false
                    secondPass.isVisible = false
                    register.isVisible = false
                    signed.isVisible = false
                    codeResend.setOnClickListener {
                        code = ""
                        flag = false
                        url = "http://185.119.56.91/api/User/RegisterMobile?email=${login.text}&"
                        request = Request.Builder()
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
                        Toast.makeText(applicationContext, "Код успешно переотправлен. Проверьте почту ${login.text}", Toast.LENGTH_SHORT).show()
                    }
                    sendCode.setOnClickListener {
                        if (enteredCode.text.toString() == code) {
                            if(api.post("http://185.119.56.91/api/User/EndRegisterMobile?email=${login.text}&nickname=${nickname.text}&password=${password.text}", ""))
                            {
                                val intent = Intent(applicationContext, LoginActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                startActivity(intent)
                                finishAffinity()
                            }
                        } else {
                            enteredCode.setText("")
                            Toast.makeText(applicationContext, "Проверочный код не совпадает", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }
    }
    private fun isValidPasswordFormat(password: String): Boolean {
        val passwordREGEX = Pattern.compile("^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                ".{8,}" +               //at least 8 characters
                "$");
        return passwordREGEX.matcher(password).matches()
    }
}