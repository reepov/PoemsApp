package com.example.myapplication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import io.ktor.client.request.forms.*
import okhttp3.*
import java.io.IOException


class Create : AppCompatActivity(){
    private lateinit var homeButton : ImageButton
    private lateinit var profileButton : ImageButton
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_layout)
        var sendPoem = findViewById<Button>(R.id.sendPoem)
        var editTitle = findViewById<EditText>(R.id.enterTitle)
        var editText = findViewById<EditText>(R.id.enterMainText)
        sendPoem.setOnClickListener {
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            var text =
                System.getProperty("line.separator")?.let { it1 -> editText.text.split(it1) }
                    ?.toMutableList()
            if (text != null) {
                for (i in 0..text.size - 2)
                    text[i] += "|"
            }
            var title = if(editTitle.text.toString() != "") editTitle.text else "Без названия"
            var url = "http://185.119.56.91/api/Poems/AuthorSendPoem?userId=e4e60c56-f038-4a1a-89b9-70a4c869d8e0&title=$title"
            val client = OkHttpClient()
            val formBody: RequestBody = FormBody.Builder()
                .add("message", text.toString())
                .build()
            val request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE" + e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    //println("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" + response.code)
                    startActivity(intent)
                    finish()
                }
            })
        }
        homeButton = findViewById(R.id.homeButton)
        profileButton = findViewById(R.id.profileButton)
        profileButton.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            intent.putExtra("userId", "e4e60c56-f038-4a1a-89b9-70a4c869d8e0")
            startActivity(intent)
            finish()
        }
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}