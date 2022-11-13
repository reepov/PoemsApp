package com.example.myapplication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PackageManagerCompat.LOG_TAG
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import okhttp3.*
import okhttp3.internal.EMPTY_REQUEST
import org.w3c.dom.Text
import java.io.IOException
import java.time.LocalDate


class Comment : AppCompatActivity(){
    var List : ArrayList<CommentModel> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val client = OkHttpClient()
        var bool = true
        val poemId = intent.getStringExtra("poemId")
        var currentUserId = intent.getStringExtra("currentUserId")
        val request = Request.Builder()
            .url("http://185.119.56.91/api/Poems/GetCommentsByPoemId?userId=$currentUserId&poemId=$poemId")
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
                List = JSON.readValue<ArrayList<CommentModel>>(responseGet)
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
        for (i in 0 until List.size)
        {
            val comment = List[i]
            child = layoutInflater.inflate(R.layout.comment_layout_item, null)
            val user = child.findViewById<TextView>(R.id.userName)
            user.text = comment.userName
            val textComment = child.findViewById<TextView>(R.id.commentText)
            textComment.text = comment.text
            val date = child.findViewById<TextView>(R.id.dateTime)
            date.text = comment.created
            val image = child.findViewById<ImageView>(R.id.avatar)
            image.setImageResource(R.mipmap.ic_launcher)
            val likeButton = child.findViewById<ImageButton>(R.id.commentLike)
            val likes : TextView = child.findViewById(R.id.countCommLikes)
            likes.text = comment.likes.toString()
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
                    likeButton.setImageResource(R.drawable.ic_like_after);
                    likes.text = like.toString()
                }
                flag = !flag
            }
            linearLayout.addView(child)
        }
        sendComment.setOnClickListener {
            val textview : EditText = findViewById(R.id.commentEnterText)
            if (textview.text.toString() != "") {
                var url = "http://185.119.56.91/api/Poems/SetCommentToPoem?userId=$currentUserId&poemId=$poemId&text=${textview.text}"
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
                        finish()
                        println("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" + response.code)
                    }
                })
            }
            textview.text.clear()
        }
    }
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null) {
            if (keyCode == KeyEvent.KEYCODE_BACK && !event.isCanceled()) {
                finish()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}