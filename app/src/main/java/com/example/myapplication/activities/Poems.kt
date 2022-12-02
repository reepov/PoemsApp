package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.dataModels.PoemsModel
import com.example.myapplication.R
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.*
import okhttp3.internal.EMPTY_REQUEST
import java.io.IOException

class Poems : AppCompatActivity() {
    var poema : PoemsModel? = null
    private lateinit var likeButton : ImageButton
    private lateinit var commentButton : ImageButton
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val poemId = intent.getStringExtra("poemId")
        println(poemId)
        var client = OkHttpClient()
        var bool = true
        val currentUserId = intent.getStringExtra("currentUserId")
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
        setContentView(R.layout.fragment_number)
        val textView: TextView = findViewById(R.id.textView)
        val title : TextView = findViewById(R.id.titleTextView)
        val likes : TextView = findViewById(R.id.countLikes)
        val comments : TextView = findViewById(R.id.countComms)
        val name : TextView = findViewById(R.id.userLink)
        val date : TextView = findViewById(R.id.publishDate)
        val describe : TextView = findViewById(R.id.descriptionText)
        textView.movementMethod = ScrollingMovementMethod()
        likes.text = poema!!.Likes.toString()
        comments.text = if(poema!!.CommentIds != null) poema!!.CommentIds!!.size.toString() else "0"
        title.text = poema!!.Title
        textView.text = poema!!.Text
        name.text = poema!!.UserName
        date.text = "${date.text} ${poema!!.Created}"
        describe.text = poema!!.Description
        likeButton = findViewById(R.id.like)
        if(poema!!.isLikedByCurrentUser) likeButton.setImageResource(R.drawable.ic_after_dasha)
        else likeButton.setImageResource(R.drawable.ic_before_dasha)
        commentButton = findViewById(R.id.comment)
        likeButton.setOnClickListener{
            if (poema!!.isLikedByCurrentUser){
                val url = "http://185.119.56.91/api/Poems/RemoveLikeFromPoem?userId=$currentUserId&poemId=${poema!!.PoemId}"
                request = Request.Builder()
                    .url(url)
                    .post(EMPTY_REQUEST)
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val like = (likes.text as String).toInt() - 1
                        likeButton.setImageResource(R.drawable.ic_before_dasha)
                        likes.text = like.toString()
                        poema!!.isLikedByCurrentUser = !poema!!.isLikedByCurrentUser
                        poema!!.Likes--
                    }
                })

            }
            else
            {
                val url = "http://185.119.56.91/api/Poems/SetLikeToPoem?userId=$currentUserId&poemId=${poema!!.PoemId}"
                request = Request.Builder()
                    .url(url)
                    .post(EMPTY_REQUEST)
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val like = (likes.text as String).toInt() + 1
                        likeButton.setImageResource(R.drawable.ic_after_dasha)
                        likes.text = like.toString()
                        poema!!.isLikedByCurrentUser = !poema!!.isLikedByCurrentUser
                        poema!!.Likes++
                    }
                })
            }
        }
        commentButton.setOnClickListener{
            val intent = Intent(applicationContext, Comment::class.java)
            intent.putExtra("poemId", poema!!.PoemId)
            intent.putExtra("currentUserId", currentUserId)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        val url = "http://185.119.56.91/api/Poems/SetViewToPoem?userId=$currentUserId&poemId=${poema!!.PoemId}"
        client = OkHttpClient()
        request = Request.Builder()
            .url(url)
            .post(EMPTY_REQUEST)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {

            }
        })
    }
}