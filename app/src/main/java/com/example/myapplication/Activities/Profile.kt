package com.example.myapplication.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.PoemsModel
import com.example.myapplication.R
import com.example.myapplication.UserModel
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.*
import java.io.IOException

class Profile : AppCompatActivity() {
    private var _list : ArrayList<PoemsModel> = arrayListOf()
    private var _user : UserModel = UserModel("", "", arrayListOf())
    private lateinit var createButton : ImageButton
    private lateinit var homeButton : ImageButton
    private lateinit var profileButton : ImageButton
    @SuppressLint("SetTextI18n", "InflateParams")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val client = OkHttpClient()
        var bool = true
        //val userId = intent.getStringExtra("userId")
        val currentUserId = intent.getStringExtra("currentUserId")
        val request = Request.Builder()
            .url("http://185.119.56.91/api/User/GetUserById?currentUserId=$currentUserId&userId=$currentUserId")
            .build()
        var responseGet : String
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("error")
            }
            override fun onResponse(call: Call, response: Response) {
                responseGet = response.body?.string().toString()
                val json = jacksonObjectMapper()
                _user = json.readValue(responseGet)
                if(response.code == 200) bool = false
            }
        })
        while(bool){
            Thread.sleep(100)
            continue
        }
        bool = true
        _list = _user.poems
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_layout)
        homeButton = findViewById(R.id.homeButton)
        createButton = findViewById(R.id.createButton)
        profileButton = findViewById(R.id.profileButton)
        profileButton.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            intent.putExtra("currentUserId", currentUserId)
            startActivity(intent)
            finish()
        }
        createButton.setOnClickListener{
            val intent = Intent(this, Create::class.java)
            intent.putExtra("currentUserId", currentUserId)
            startActivity(intent)
            finish()
        }
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        val nick = findViewById<TextView>(R.id.profileNickName)
        println(nick.text)
        val likes = findViewById<TextView>(R.id.profileCountLikes)
        val countPoems = findViewById<TextView>(R.id.profileCountPoems)
        var wholeLikes = 0
        nick.text = _user.nickName
        countPoems.text = "${_list.size} posts"
        val linearLayout : LinearLayout = findViewById(R.id.linearLayoutProfile)
        var child: View
        for (i in 0 until _list.size)
        {
            val poem = _list[i]
            wholeLikes += poem.likes
            child = layoutInflater.inflate(R.layout.profile_layout_poemitem, null)
            val title = child.findViewById<TextView>(R.id.poemTitle)
            title.text = title.text.toString() + poem.title
            title.setOnClickListener {
                val intent = Intent(applicationContext, Poems::class.java)
                intent.putExtra("poemId", poem.poemId)
                intent.putExtra("currentUserId", currentUserId)
                startActivity(intent)
            }
            linearLayout.addView(child)
        }
        likes.text = "$wholeLikes likes"
    }
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null) {
            if (keyCode == KeyEvent.KEYCODE_BACK && !event.isCanceled) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}