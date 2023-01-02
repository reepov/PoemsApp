package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.services.PoemAdapter
import com.example.myapplication.dataModels.PoemsModel
import com.example.myapplication.R
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.*
import java.io.IOException

class MainActivity : FragmentActivity() {

    private lateinit var adapter: PoemAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var createButton : ImageButton
    private lateinit var homeButton : ImageButton
    private lateinit var profileButton : ImageButton
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recommsList : TextView
    private lateinit var subsButton : ImageButton
    private lateinit var subsList : TextView
    var list : ArrayList<PoemsModel> = arrayListOf()
    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("USER_INFO_SP", Context.MODE_PRIVATE)
        val currentUserId = sharedPreferences.getString("CurrentUserId", "")
        if(!sharedPreferences.getBoolean("isRemembered", false))
        {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
        else {
            while (currentUserId == "") Thread.sleep(100)
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("http://185.119.56.91/api/Poems/GetListOfRandomPoems?userId=$currentUserId")
                .build()
            var responseGet: String
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {
                    responseGet = response.body?.string().toString()
                    val json = jacksonObjectMapper()
                    list = json.readValue(responseGet)
                    set(list)
                }
            })
            while (list.isEmpty()) continue
            adapter = PoemAdapter(this)
            viewPager = findViewById(R.id.pager)
            homeButton = findViewById(R.id.homeButton)
            viewPager.adapter = adapter
            adapter.list = list
            adapter.currentUserId = currentUserId!!
            createButton = findViewById(R.id.createButton)
            profileButton = findViewById(R.id.profileButton)
            subsButton = findViewById(R.id.subscribersButton)
            recommsList = findViewById(R.id.recommendations)
            subsList = findViewById(R.id.subscribers)
            var typeFace: Typeface? = ResourcesCompat.getFont(applicationContext, R.font.montserrat)
            subsList.typeface = typeFace
            typeFace = ResourcesCompat.getFont(applicationContext, R.font.bold)
            recommsList.typeface = typeFace
            recommsList.setOnClickListener {
                typeFace= ResourcesCompat.getFont(applicationContext, R.font.montserrat)
                subsList.typeface = typeFace
                typeFace = ResourcesCompat.getFont(applicationContext, R.font.bold)
                recommsList.typeface = typeFace
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finishAffinity()
            }
            subsList.setOnClickListener {
                val intent = Intent(this, SubscribedActivity::class.java)
                intent.putExtra("currentUserId", currentUserId)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
            subsButton.setOnClickListener {
                val intent = Intent(this, SubscribedActivity::class.java)
                intent.putExtra("currentUserId", currentUserId)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
            profileButton.setOnClickListener {
                val intent = Intent(this, ProfileActivity::class.java)
                println(currentUserId)
                intent.putExtra("currentUserId", currentUserId)
                intent.putExtra("userId", currentUserId)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
            createButton.setOnClickListener {
                val intent = Intent(this, CreateActivity::class.java)
                intent.putExtra("currentUserId", currentUserId)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
            homeButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
            }
        }
    }
    fun set(lists : ArrayList<PoemsModel>)
    {
        list = lists
    }
}


//TODO faster
// NotifyButton - how and when to send notifications to user?
// Tips at start
// Make search activity
// Add more posts and users
// Forgot the password?
// Dark Theme
// Password harder
// Set avatars (NOT SOLVED AT ALL - SIZE AND SAVE ISSUES)
// Focus on EditText (NOT SOLVED AT ALL - CAN'T DO ANYTHING)
// ShareButton - how to share posts within the link? (READY)
// Move subs to recommendations (READY)
// Add already have an account at register (READY)
// Resend code (READY)
// Email unique account check (READY)


//TODO not so fast
// Better to remove animation between activities
// How to resend data for each 3 posts?
// More settings
// Rewrite POST to form-dataS
// Playlists
// Pay-to-read
// Privacy policySS
// Advertisement
// VIP subs
// VIP author


