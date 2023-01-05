package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.dataModels.PoemsModel
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.*
import java.io.IOException

class FinderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.finder_layout)
        var searchText = findViewById<EditText>(R.id.searchText)
        var publishLayout = findViewById<LinearLayout>(R.id.layoutPubs)
        var authorLayout = findViewById<LinearLayout>(R.id.layoutAuthors)
        var currentUserId = intent.getStringExtra("currentUserId")!!
        var resultPoems = arrayListOf<PoemsModel>()
        var searchButton = findViewById<Button>(R.id.search)
        var client = OkHttpClient()
        var bool = true
        var request = Request.Builder()
            .url("http://185.119.56.91/api/Poems/ResultOfSearch?userId=$currentUserId&searchText=")
            .build()
        var responseGet: String
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("error")
            }

            override fun onResponse(call: Call, response: Response) {
                responseGet = response.body?.string().toString()
                println(responseGet)
                val json = jacksonObjectMapper()
                resultPoems = json.readValue(responseGet)
                if (response.code == 200) bool = false
            }
        })
        while (bool) {
            Thread.sleep(100)
            continue
        }
        bool = true
        publishLayout.removeAllViews()
        resultPoems.forEach{
            val poem = it
            var child = layoutInflater.inflate(R.layout.profile_layout_poemitem, null)
            val views = child.findViewById<TextView>(R.id.countViews)
            val title = child.findViewById<TextView>(R.id.poemTitle)
            title.text = title.text.toString() + poem.Title
            views.text = poem.Views.toString()
            title.setOnClickListener {
                val intent = Intent(applicationContext, PoemActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.putExtra("poemId", poem.PoemId)
                intent.putExtra("currentUserId", currentUserId)
                startActivity(intent)
            }
            publishLayout.addView(child)
        }
        searchButton.setOnClickListener {
            client = OkHttpClient()
            bool = true
            request = Request.Builder()
                .url("http://185.119.56.91/api/Poems/ResultOfSearch?userId=$currentUserId&searchText=${searchText.text}")
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("error" + e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    responseGet = response.body?.string().toString()
                    println(responseGet)
                    val json = jacksonObjectMapper()
                    resultPoems = json.readValue(responseGet)
                    if (response.code == 200) bool = false
                }
            })
            while (bool) {
                Thread.sleep(100)
                continue
            }
            bool = true
            publishLayout.removeAllViews()
            resultPoems.forEach{
                val poem = it
                val child = layoutInflater.inflate(R.layout.profile_layout_poemitem, null)
                val views = child.findViewById<TextView>(R.id.countViews)
                val title = child.findViewById<TextView>(R.id.poemTitle)
                title.text = title.text.toString() + poem.Title
                views.text = poem.Views.toString()
                title.setOnClickListener {
                    val intent = Intent(applicationContext, PoemActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra("poemId", poem.PoemId)
                    intent.putExtra("currentUserId", currentUserId)
                    startActivity(intent)
                }
                publishLayout.addView(child)
            }
        }
    }
}