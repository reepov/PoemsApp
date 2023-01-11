package com.example.myapplication.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.services.APISender
import io.ktor.client.request.forms.*
import okhttp3.*

class CreateActivity : AppCompatActivity(){
    private lateinit var homeButton : ImageButton
    private lateinit var profileButton : ImageButton
    private lateinit var subsButton : ImageButton
    private lateinit var notifyButton : ImageButton
    private var currentUserId = ""
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_layout)
        val sendPoem = findViewById<Button>(R.id.sendPoem)
        val editTitle = findViewById<EditText>(R.id.enterTitle)
        val editText = findViewById<EditText>(R.id.enterMainText)
        val description = findViewById<EditText>(R.id.enterDescription)
        currentUserId = intent.getStringExtra("currentUserId")!!
        println(currentUserId)
        sendPoem.setOnClickListener {
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val text =
                System.getProperty("line.separator")?.let { it1 -> editText.text.split(it1) }
                    ?.toMutableList()
            if (text != null) {
                for (i in 0..text.size - 2)
                    text[i] += "|"
            }
            val title = if(editTitle.text.toString() != "") editTitle.text else "Без названия"
            val api = APISender()
            if(api.post("http://185.119.56.91/api/Poems/AuthorSendPoem?userId=$currentUserId&title=${title.toString()}&description=${description.text}", text.toString()))
            {
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
            }
        }
        subsButton = findViewById(R.id.subscribersButton)
        homeButton = findViewById(R.id.homeButton)
        profileButton = findViewById(R.id.profileButton)
        notifyButton = findViewById(R.id.notifyButton)
        notifyButton.setOnClickListener {
            Toast.makeText(applicationContext, "Уведомления пока недоступны. Следите за обновлениями!", Toast.LENGTH_LONG).show()
        }
        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("currentUserId", currentUserId)
            intent.putExtra("userId", currentUserId)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finishAffinity()
        }
        subsButton.setOnClickListener {
            val intent = Intent(this, FinderActivity::class.java)
            intent.putExtra("currentUserId", currentUserId)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finishAffinity()
        }
    }
}