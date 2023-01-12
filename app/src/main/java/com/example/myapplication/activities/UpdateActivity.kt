package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.myapplication.R
import com.example.myapplication.dataModels.PoemsModel
import com.example.myapplication.services.APISender
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

class UpdateActivity : AppCompatActivity() {
    private lateinit var poema : PoemsModel
    private lateinit var poemId : String
    private lateinit var currentUserId : String
    override fun onCreate(savedInstanceState: Bundle?) {
        poemId = intent.getStringExtra("poemId")!!
        currentUserId = intent.getStringExtra("currentUserId")!!
        val api = APISender()
        val json = jacksonObjectMapper()
        poema = json.readValue(api.get("http://185.119.56.91/api/Poems/GetPoemById?userId=$currentUserId&poemId=$poemId"))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_layout)
        val sendPoem = findViewById<Button>(R.id.sendPoem)
        val descript = findViewById<EditText>(R.id.enterDescript)
        descript.setText(poema.Description ?: "")
        val editTitle = findViewById<EditText>(R.id.enterTitle)
        editTitle.setText(poema.Title)
        val editText = findViewById<EditText>(R.id.enterMainText)
        editText.setText(poema.Text)
        descript.addTextChangedListener {
            if(it?.length!! > 120) {
                descript.setText(it.substring(0, 120))
                descript.setSelection(it.length - 1)
                Toast.makeText(applicationContext, "Вы превысили лимит символов в описании - 120 символов.", Toast.LENGTH_SHORT).show()
            }
        }
        editTitle.addTextChangedListener {
            if(it?.length!! > 80) {
                editTitle.setText(it.substring(0, 80))
                editTitle.setSelection(it.length - 1)
                Toast.makeText(applicationContext, "Вы превысили лимит символов в названии - 80 символов.", Toast.LENGTH_SHORT).show()
            }
        }
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
            if(api.post("http://185.119.56.91/api/Poems/UpdatePoem?poemId=$poemId&title=${title}&description=${descript.text}", text.toString()))
            {
                val intent = Intent(applicationContext, ProfileActivity::class.java)
                intent.putExtra("currentUserId", currentUserId)
                intent.putExtra("userId", currentUserId)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
                overridePendingTransition(0, 0)
            }
        }
    }
}