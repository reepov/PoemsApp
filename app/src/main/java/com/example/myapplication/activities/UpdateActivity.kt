package com.example.myapplication.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.dataModels.PoemsModel
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.*
import java.io.IOException

class UpdateActivity : AppCompatActivity() {
    private lateinit var poema : PoemsModel
    private lateinit var poemId : String
    private lateinit var currentUserId : String
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        var client = OkHttpClient()
        var bool = true
        poemId = intent.getStringExtra("poemId")!!
        currentUserId = intent.getStringExtra("currentUserId")!!
        var request = Request.Builder()
            .url("http://185.119.56.91/api/Poems/GetPoemById?userId=$currentUserId&poemId=$poemId")
            .build()
        var responseGet : String
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("error")
            }
            override fun onResponse(call: Call, response: Response) {
                responseGet = response.body?.string().toString()
                val json = jacksonObjectMapper()
                poema = json.readValue<PoemsModel>(responseGet)
                if(response.code == 200) bool = false
            }
        })
        while(bool){
            Thread.sleep(100)
            continue
        }
        bool = true
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_layout)
        val sendPoem = findViewById<Button>(R.id.sendPoem)
        val descript = findViewById<EditText>(R.id.enterDescript)
        descript.setText(poema.Description ?: "")
        val editTitle = findViewById<EditText>(R.id.enterTitle)
        editTitle.setText(poema.Title)
        val editText = findViewById<EditText>(R.id.enterMainText)
        editText.setText(poema.Text)
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

            val title = if(editTitle.text.toString() != "") editTitle.text else "Без названия"
            val url = "http://185.119.56.91/api/Poems/UpdatePoem?poemId=$poemId&title=${title}&description=${descript.text}"
            client = OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build()
            val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("message", text.toString())
                .build()
            request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }
                override fun onResponse(call: Call, response: Response) {
                    val intent = Intent(applicationContext, ProfileActivity::class.java)
                    intent.putExtra("currentUserId", currentUserId)
                    intent.putExtra("userId", currentUserId)
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
                val intent = Intent(this, ProfileActivity::class.java)
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