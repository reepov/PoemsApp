package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.dataModels.CommentModel
import com.example.myapplication.R
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.*
import okhttp3.internal.EMPTY_REQUEST
import java.io.IOException


class Comment : AppCompatActivity(){
    var list : ArrayList<CommentModel> = arrayListOf()

    @SuppressLint("InflateParams")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        var client = OkHttpClient()
        var bool = true
        val poemId = intent.getStringExtra("poemId")
        val currentUserId = intent.getStringExtra("currentUserId")
        var request = Request.Builder()
            .url("http://185.119.56.91/api/Poems/GetCommentsByPoemId?userId=$currentUserId&poemId=$poemId")
            .build()
        var responseGet : String
        println(poemId)
        println(currentUserId)
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("error")
            }
            override fun onResponse(call: Call, response: Response) {
                responseGet = response.body?.string().toString()
                val json = jacksonObjectMapper()
                list = json.readValue(responseGet)
                if(response.code == 200) bool = false
            }
        })
        while(bool){
            Thread.sleep(100)
            continue
        }
        bool = true
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comment_layout)
        val linearLayout : LinearLayout = findViewById(R.id.linearLayoutComment)
        val sendComment : Button = findViewById(R.id.sendComment)
        var child: View
        for (i in 0 until list.size)
        {
            val comment = list[i]
            child = layoutInflater.inflate(R.layout.comment_layout_item, null)
            val user = child.findViewById<TextView>(R.id.userName)
            user.text = comment.UserName
            val textComment = child.findViewById<TextView>(R.id.commentText)
            textComment.text = comment.Text
            val date = child.findViewById<TextView>(R.id.dateTime)
            date.text = comment.Created
            val image = child.findViewById<ImageView>(R.id.avatar)
            image.setImageResource(R.mipmap.ic_launcher)
            val likeButton = child.findViewById<ImageButton>(R.id.commentLike)
            val likes : TextView = child.findViewById(R.id.countCommLikes)
            likes.text = comment.Likes.toString()
            var flag = true
            likeButton.setOnClickListener{
                if (!flag){
                    val like = (likes.text as String).toInt() - 1
                    likeButton.setImageResource(R.drawable.ic_like_before)
                    likes.text = like.toString()
                }
                else
                {
                    val like = (likes.text as String).toInt() + 1
                    likeButton.setImageResource(R.drawable.ic_like_after)
                    likes.text = like.toString()
                }
                flag = !flag
            }
            linearLayout.addView(child)
        }
        sendComment.setOnClickListener {
            val textview : EditText = findViewById(R.id.commentEnterText)
            if (textview.text.toString() != "") {
                val url = "http://185.119.56.91/api/Poems/SetCommentToPoem?userId=$currentUserId&poemId=$poemId&text=${textview.text}"
                client = OkHttpClient()
                request = Request.Builder()
                    .url(url)
                    .post(EMPTY_REQUEST)
                    .build()
                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {

                    }
                    override fun onResponse(call: Call, response: Response) {
                        finish()
                    }
                })
            }
            textview.text.clear()
        }
    }
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                finish()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}