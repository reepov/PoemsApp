package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.dataModels.PoemsModel
import com.example.myapplication.R
import com.example.myapplication.dataModels.UserModel
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.*
import okhttp3.internal.EMPTY_REQUEST
import java.io.IOException

class Profile : AppCompatActivity() {
    private var _list : ArrayList<PoemsModel> = arrayListOf()
    private var _user : UserModel = UserModel("", "", arrayListOf(), arrayListOf(), false)
    private lateinit var createButton : ImageButton
    private lateinit var homeButton : ImageButton
    private lateinit var profileButton : ImageButton
    private lateinit var settingsButton : ImageButton
    @SuppressLint("SetTextI18n", "InflateParams", "MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        var client = OkHttpClient()
        var bool = true
        val userId = intent.getStringExtra("userId")
        val currentUserId = intent.getStringExtra("currentUserId")
        var request = Request.Builder()
            .url("http://185.119.56.91/api/User/GetUserById?currentUserId=$currentUserId&userId=$userId")
            .build()
        var responseGet : String
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("error")
            }
            override fun onResponse(call: Call, response: Response) {
                responseGet = response.body?.string().toString()
                println(responseGet)
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
        _list = _user.Poems
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_layout)
        homeButton = findViewById(R.id.homeButton)
        createButton = findViewById(R.id.createButton)
        profileButton = findViewById(R.id.profileButton)
        settingsButton = findViewById(R.id.buttonSettings)
        val subscribe : TextView = findViewById(R.id.subscribe)
        subscribe.text = if(_user.isSubscribedByCurrentUser) "Отписаться" else "Подписаться"
        subscribe.setOnClickListener {
            val url = "http://185.119.56.91/api/User/SubscribeToUser?userId=${_user.Id}&currentUserId=$currentUserId"
            var bool = true
            client = OkHttpClient()
            request = Request.Builder()
                .url(url)
                .post(EMPTY_REQUEST)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                override fun onResponse(call: Call, response: Response) {
                    _user.isSubscribedByCurrentUser = response.body?.string().toString() == "true"
                    if(response.code == 200) bool = false
                }
            })
            while(bool) Thread.sleep(100)
            subscribe.text = if(_user.isSubscribedByCurrentUser) "Отписаться" else "Подписаться"
            bool = true
        }
        settingsButton.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        profileButton.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra("currentUserId", currentUserId)
            intent.putExtra("userId", currentUserId)
            startActivity(intent)
            finish()
        }
        createButton.setOnClickListener{
            val intent = Intent(this, Create::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra("currentUserId", currentUserId)
            startActivity(intent)
            finish()
        }
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
        val nick = findViewById<TextView>(R.id.profileNickName)
        println(nick.text)
        val likes = findViewById<TextView>(R.id.profileCountLikes)
        val countPoems = findViewById<TextView>(R.id.profileCountPoems)
        val countSubs = findViewById<TextView>(R.id.profileCountSubscribers)
        val countViews = findViewById<TextView>(R.id.profileCountViews)
        var wholeLikes = 0
        var wholeViews = 0
        nick.text = _user.NickName
        countPoems.text = "Posts: ${_list.size}"
        countSubs.text = "Subscribers: ${_user.Subscribers.size}"
        val linearLayout : LinearLayout = findViewById(R.id.linearLayoutProfile)
        var child: View
        for (i in 0 until _list.size)
        {
            val poem = _list[i]
            wholeLikes += poem.Likes
            wholeViews += poem.Views
            child = layoutInflater.inflate(R.layout.profile_layout_poemitem, null)
            val title = child.findViewById<TextView>(R.id.poemTitle)
            title.text = title.text.toString() + poem.Title
            title.setOnClickListener {
                val intent = Intent(applicationContext, Poems::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.putExtra("poemId", poem.PoemId)
                intent.putExtra("currentUserId", currentUserId)
                startActivity(intent)
            }
            title.setOnLongClickListener{
                val builder1: AlertDialog.Builder = AlertDialog.Builder(this)
                builder1.setMessage("Вы хотите удалить или отредактировать пост?")
                builder1.setCancelable(true)
                builder1.setNegativeButton("Удалить") { dialog, _ ->
                    var text = ""
                    val url = "http://185.119.56.91/api/Poems/DeletePoem?poemId=${poem.PoemId}"
                    client = OkHttpClient()
                    request = Request.Builder()
                        .url(url)
                        .post(EMPTY_REQUEST)
                        .build()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            text = "Что-то пошло не так"
                        }
                        override fun onResponse(call: Call, response: Response) {
                            text = "Пост удален"
                        }
                    })
                    while (text == "") Thread.sleep(100)
                    if(text != "Что-то пошло не так") linearLayout.removeView(it)
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                    dialog.cancel()
                }
                builder1.setPositiveButton("Отредактировать") {dialog, _ ->
                    val intent = Intent(this, Update::class.java)
                    intent.putExtra("poemId", poem.PoemId)
                    println(poem.PoemId)
                    intent.putExtra("currentUserId", currentUserId)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    finish()
                    dialog.cancel()
                }
                builder1.setNeutralButton("Отмена") { dialog, _ -> dialog.cancel() }
                val alert11: AlertDialog = builder1.create()
                alert11.show()
                return@setOnLongClickListener true
            }
            linearLayout.addView(child)
        }
        likes.text = "Likes: $wholeLikes"
        countViews.text = "Views: $wholeViews"
    }
}