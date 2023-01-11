package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.R
import com.example.myapplication.dataModels.PoemsModel
import com.example.myapplication.services.APISender
import com.example.myapplication.services.PoemAdapter
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.*

class MainActivity : FragmentActivity() {

    private lateinit var adapter: PoemAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var createButton : ImageButton
    private lateinit var homeButton : ImageButton
    private lateinit var profileButton : ImageButton
    private lateinit var notifyButton : ImageButton
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recommendationsList : TextView
    private lateinit var subsButton : ImageButton
    private lateinit var subsList : TextView

    private var list : ArrayList<PoemsModel> = arrayListOf()
    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        println("onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("USER_INFO_SP", Context.MODE_PRIVATE)
        val currentUserId = sharedPreferences.getString("CurrentUserId", "")
        val json = jacksonObjectMapper()
        val api = APISender()
        if(!sharedPreferences.getBoolean("isRemembered", false))
        {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
        else {
            while (currentUserId == "") Thread.sleep(100)
            list = json.readValue(api.get("http://185.119.56.91/api/Poems/GetListOfRandomPoems?userId=$currentUserId"))
            adapter = PoemAdapter(this)
            viewPager = findViewById(R.id.pager)
            homeButton = findViewById(R.id.homeButton)
            viewPager.adapter = adapter
            adapter.list = list
            adapter.currentUserId = currentUserId!!
            createButton = findViewById(R.id.createButton)
            profileButton = findViewById(R.id.profileButton)
            subsButton = findViewById(R.id.subscribersButton)
            recommendationsList = findViewById(R.id.recommendations)
            subsList = findViewById(R.id.subscribers)
            var typeFace: Typeface? = ResourcesCompat.getFont(applicationContext, R.font.montserrat)
            subsList.typeface = typeFace
            typeFace = ResourcesCompat.getFont(applicationContext, R.font.bold)
            recommendationsList.typeface = typeFace
            recommendationsList.setOnClickListener {
                typeFace= ResourcesCompat.getFont(applicationContext, R.font.montserrat)
                subsList.typeface = typeFace
                typeFace = ResourcesCompat.getFont(applicationContext, R.font.bold)
                recommendationsList.typeface = typeFace
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finishAffinity()
            }
            notifyButton = findViewById(R.id.notifyButton)
            notifyButton.setOnClickListener {
                Toast.makeText(applicationContext, "Уведомления пока недоступны. Следите за обновлениями!", Toast.LENGTH_LONG).show()
            }
            subsList.setOnClickListener {
                val intent = Intent(this, SubscribedActivity::class.java)
                intent.putExtra("currentUserId", currentUserId)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
            subsButton.setOnClickListener {
                val intent = Intent(this, FinderActivity::class.java)
                intent.putExtra("currentUserId", currentUserId)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
            profileButton.setOnClickListener {
                val intent = Intent(this, ProfileActivity::class.java)
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
}

//TODO faster
// Tips at start
// Add more posts and users

//TODO not solved at all
// Forgot the password? (NOT SOLVED AT ALL - MAKE LINKS CLOSED AND ONCE-USED)
// Set avatars (NOT SOLVED AT ALL - SIZE AND SAVE ISSUES)
// Focus on EditText (NOT SOLVED AT ALL - CAN'T DO ANYTHING)
// Load widget (NOT SOLVED AT ALL - CAN'T DO ANYTHING)

//TODO not so fast
// NotifyButton - how and when to send notifications to user?
// Better to remove animation between activities
// How to resend data for each 3 posts?
// More settings
// Rewrite POST to form-data
// Playlists
// Pay-to-read
// Privacy policy
// Advertisement
// VIP subs
// VIP author


