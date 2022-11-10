package com.example.myapplication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.KeyEvent
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.*
import okhttp3.internal.EMPTY_REQUEST
import java.io.IOException

class Poems : AppCompatActivity() {
    var poema : PoemsModel? = null
    private lateinit var likeButton : ImageButton
    private lateinit var commentButton : ImageButton
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val poemId = intent.getStringExtra("poemId")
        println(poemId)
        val client = OkHttpClient()
        var bool = true
        val userId = intent.getStringExtra("userId")
        println(userId)
        val request = Request.Builder()
            .url("http://185.119.56.91/api/Poems/GetPoemById?userId=$userId&poemId=$poemId")
            .build()
        var responseGet : String = ""
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("error")
            }
            override fun onResponse(call: Call, response: Response) {
                responseGet = response?.body?.string().toString()
                println(response.code.toString() + " " + responseGet + "  dsjkahslfkgjhdflkjghldkfjhglkdjfhglksdfjhg")
                val JSON = jacksonObjectMapper()
                poema = JSON.readValue<PoemsModel>(responseGet)
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
        val comms : TextView = findViewById(R.id.countComms)
        textView.movementMethod = ScrollingMovementMethod()
        var child: View
        likes.text = poema!!.likes.toString();
        comms.text = if(poema!!.commentIds != null) poema!!.commentIds!!.size.toString() else "0"
        title.text = poema!!.title
        textView.text = poema!!.text
        var flag = true
        likeButton = findViewById(R.id.like)
        if(poema!!.isLikedByCurrentUser) likeButton.setImageResource(R.drawable.ic_like_after)
        else likeButton.setImageResource(R.drawable.ic_like_before);
        commentButton = findViewById(R.id.comment)
        likeButton.setOnClickListener{
            if (poema!!.isLikedByCurrentUser){
                val url = "http://185.119.56.91/api/Poems/RemoveLikeFromPoem?userId=e4e60c56-f038-4a1a-89b9-70a4c869d8e0&poemId=${poema!!.poemId}"
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(url)
                    .post(EMPTY_REQUEST)
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE" + e.message)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val like = (likes.text as String).toInt() - 1
                        likeButton.setImageResource(R.drawable.ic_like_before)
                        likes.text = like.toString()
                        poema!!.isLikedByCurrentUser = !poema!!.isLikedByCurrentUser
                        poema!!.likes--
                        //println("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" + response.code)
                    }
                })

            }
            else
            {
                var url = "http://185.119.56.91/api/Poems/SetLikeToPoem?userId=e4e60c56-f038-4a1a-89b9-70a4c869d8e0&poemId=${poema!!.poemId}"
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(url)
                    .post(EMPTY_REQUEST)
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE" + e.message)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val like = (likes.text as String).toInt() + 1
                        likeButton.setImageResource(R.drawable.ic_like_after);
                        likes.text = like.toString()
                        poema!!.isLikedByCurrentUser = !poema!!.isLikedByCurrentUser
                        poema!!.likes++
                        //println("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" + response.code)
                    }
                })
            }
        }
        commentButton.setOnClickListener{
            val intent = Intent(applicationContext, Comment::class.java)
            intent.putExtra("poemId", poema!!.poemId)
            startActivity(intent)
        }
    }
}