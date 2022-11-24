package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.R
import com.example.myapplication.dataModels.PoemsModel
import com.example.myapplication.services.NumberAdapter
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.*
import java.io.IOException

class Subscribers : FragmentActivity(){
    private lateinit var adapter: NumberAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var createButton : ImageButton
    private lateinit var homeButton : ImageButton
    private lateinit var profileButton : ImageButton
    private lateinit var subsButton : ImageButton
    private lateinit var type : TextView
    var list : ArrayList<PoemsModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var currentUserId = intent.getStringExtra("currentUserId")
        println(currentUserId)
        var f = true
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://185.119.56.91/api/Poems/GetListOfSubscribedPoems?userId=$currentUserId")
            .build()
        var responseGet: String
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("error in random")
            }

            override fun onResponse(call: Call, response: Response) {
                responseGet = response.body?.string().toString()
                println(responseGet)
                val json = jacksonObjectMapper()
                list = json.readValue(responseGet)
                set(list)
                f = false
            }
        })
        while(f) Thread.sleep(100)
        f = true
        adapter = NumberAdapter(this)
        viewPager = findViewById(R.id.pager)
        homeButton = findViewById(R.id.homeButton)
        viewPager.adapter = adapter
        adapter.list = list
        adapter.currentUserId = currentUserId!!
        createButton = findViewById(R.id.createButton)
        profileButton = findViewById(R.id.profileButton)
        subsButton = findViewById(R.id.subscribersButton)
        type = findViewById(R.id.type)
        type.text = "Подписки"
        subsButton.setOnClickListener {
            val intent = Intent(this, Subscribers::class.java)
            intent.putExtra("currentUserId", currentUserId)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
        profileButton.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            println(currentUserId)
            intent.putExtra("currentUserId", currentUserId)
            intent.putExtra("userId", currentUserId)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        createButton.setOnClickListener {
            val intent = Intent(this, Create::class.java)
            intent.putExtra("currentUserId", currentUserId)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
    }
    fun set(lists : ArrayList<PoemsModel>)
    {
        list = lists
    }
}
