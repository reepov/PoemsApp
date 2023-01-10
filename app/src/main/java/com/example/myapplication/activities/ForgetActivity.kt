package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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

class ForgetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forget_layout)
        val emailText = findViewById<EditText>(R.id.enterEmail)
        val sendButton = findViewById<Button>(R.id.sendToReset)
        val text = findViewById<TextView>(R.id.textForget)
        val textReset = findViewById<TextView>(R.id.textReset)
        val newPass = findViewById<EditText>(R.id.enterNewPassword)
        val secondNewPass = findViewById<EditText>(R.id.repeatNewPassword)
        val resetPassword = findViewById<Button>(R.id.resetButton)
        val action : String? = intent.action
        val data: String? = intent.dataString
        val api = APISender()
        var email = ""
        if (Intent.ACTION_VIEW == action && data != null){
            email = data.split("email=")[1]
            println(email)
            emailText.isVisible = false
            sendButton.isVisible = false
            text.isVisible = false
            textReset.isVisible = true
            newPass.isVisible = true
            secondNewPass.isVisible = true
            resetPassword.isVisible = true
        }
        else {
            newPass.isVisible = false
            resetPassword.isVisible = false
            secondNewPass.isVisible = false
            text.isVisible = true
            textReset.isVisible = false
            emailText.isVisible = true
            sendButton.isVisible = true

        }
        sendButton.setOnClickListener {
            if (!emailText.text.contains("@") || emailText.text.indexOf("@") == 0 || emailText.text.indexOf("@") == emailText.text.length - 1 ) Toast.makeText(
                applicationContext,
                "Введен некорректный адрес электронной почты",
                Toast.LENGTH_SHORT
            ).show()
            else{
                if(api.post("http://185.119.56.91/api/User/ResetPasswordRequest?email=${emailText.text}", "")){
                    Toast.makeText(applicationContext, "На вашу электронную почту было отправлено письмо со ссылкой на смену пароля", Toast.LENGTH_SHORT).show()
                }
                else Toast.makeText(applicationContext, "Произошла неизвестная ошибка. Повторите снова.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
        }
        resetPassword.setOnClickListener {
            if (api.post("http://185.119.56.91/api/User/ResetPassword?email=${email}&password=${newPass.text}", "")){
                Toast.makeText(applicationContext, "Ваш пароль был успешно изменен", Toast.LENGTH_SHORT).show()
            }
            else Toast.makeText(applicationContext, "Произошла неизвестная ошибка. Повторите снова.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }
}