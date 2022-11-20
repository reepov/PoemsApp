package com.example.myapplication.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import okhttp3.*
import java.io.IOException

class Update : AppCompatActivity() {
    private lateinit var homeButton : ImageButton
    private lateinit var profileButton : ImageButton
    private lateinit var poemId : String
    private lateinit var currentUserId : String
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_layout)
        val sendPoem = findViewById<Button>(R.id.sendPoem)
        val editTitle = findViewById<EditText>(R.id.enterTitle)
        val editText = findViewById<EditText>(R.id.enterMainText)
        sendPoem.setOnClickListener {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val text =
                System.getProperty("line.separator")?.let { it1 -> editText.text.split(it1) }
                    ?.toMutableList()
            if (text != null) {
                for (i in 0..text.size - 2)
                    text[i] += "|"
            }
            poemId = intent.getStringExtra("poemId")!!
            println(poemId)
            currentUserId = intent.getStringExtra("currentUserId")!!
            println(currentUserId)
            val title = if(editTitle.text.toString() != "") editTitle.text else "Без названия"
            val url = "http://185.119.56.91/api/Poems/UpdatePoem?poemId=$poemId&title=${title}"
            val client: OkHttpClient = OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build()
            val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("message", text.toString())
                .build()
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }
                override fun onResponse(call: Call, response: Response) {
                    val intent = Intent(applicationContext, Profile::class.java)
                    intent.putExtra("currentUserId", currentUserId)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    finish()
                }
            })
        }
    }
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                val intent = Intent(this, Profile::class.java)
                intent.putExtra("currentUserId", currentUserId)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}