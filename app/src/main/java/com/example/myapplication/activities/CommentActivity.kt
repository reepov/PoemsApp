package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.dataModels.CommentModel
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.*
import okhttp3.internal.EMPTY_REQUEST
import java.io.IOException


class CommentActivity : AppCompatActivity(){
    var list : ArrayList<CommentModel> = arrayListOf()

    @SuppressLint("InflateParams")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        var client = OkHttpClient()
        var bool = true
        val poemId = intent.getStringExtra("poemId")
        val currentUserId = intent.getStringExtra("currentUserId")
        var nowAnswers = false
        var request = Request.Builder()
            .url("http://185.119.56.91/api/Poems/GetCommentsByPoemId?userId=$currentUserId&poemId=$poemId")
            .build()
        var responseGet : String
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
            user.text = if(comment.Created != "-1") comment.UserName else "DELETED"
            if (comment.Created != "-1") user.setOnClickListener {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("currentUserId", currentUserId)
                intent.putExtra("userId", comment.UserId)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
            val textComment = child.findViewById<TextView>(R.id.commentText)
            textComment.text = comment.Text
            val date = child.findViewById<TextView>(R.id.dateTime)
            date.text = if(comment.Created == "-1") " " else comment.Created
            val image = child.findViewById<ImageView>(R.id.avatar)
            if(comment.Photo != null && comment.Created != "-1") {
                val decodedByte: ByteArray = Base64.decode(comment.Photo, 0)
                val b = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
                image.setImageBitmap(
                    Bitmap.createScaledBitmap(
                        b,
                        b.width / b.height * 50,
                        50,
                        false
                    )
                )
            }
            else image.setImageResource(R.mipmap.ic_launcher)
            if (comment.Created != "-1") image.setOnClickListener {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("currentUserId", currentUserId)
                intent.putExtra("userId", comment.UserId)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
            val likeButton = child.findViewById<ImageButton>(R.id.commentLike)
            if (comment.isLikedByCurrentUser) likeButton.setImageResource(R.drawable.ic_after_dasha) else likeButton.setImageResource(R.drawable.ic_before_dasha)
            val likes : TextView = child.findViewById(R.id.countCommLikes)
            likes.text = comment.Likes.toString()
            if(comment.Created != "-1") likeButton.setOnClickListener{
                if (comment.isLikedByCurrentUser){
                    val url = "http://185.119.56.91/api/Poems/RemoveLikeFromComment?userId=$currentUserId&commentId=${comment.CommentId}"
                    var like = 0
                    var flag = true
                    client = OkHttpClient()
                    request = Request.Builder()
                        .url(url)
                        .post(EMPTY_REQUEST)
                        .build()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                        }

                        override fun onResponse(call: Call, response: Response) {
                            like = (likes.text as String).toInt() - 1
                            likeButton.setImageResource(R.drawable.ic_before_dasha)
                            comment.isLikedByCurrentUser = !comment.isLikedByCurrentUser
                            comment.Likes--
                            flag = false
                        }
                    })
                    while(flag) Thread.sleep(100)
                    likes.text = like.toString()
                    flag = true
                }
                else if(comment.Created != "-1")
                {
                    val url = "http://185.119.56.91/api/Poems/SetLikeToComment?userId=$currentUserId&commentId=${comment.CommentId}"
                    client = OkHttpClient()
                    var flag = true
                    var like = 0
                    request = Request.Builder()
                        .url(url)
                        .post(EMPTY_REQUEST)
                        .build()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                        }

                        override fun onResponse(call: Call, response: Response) {
                            like = (likes.text as String).toInt() + 1
                            likeButton.setImageResource(R.drawable.ic_after_dasha)
                            comment.isLikedByCurrentUser = !comment.isLikedByCurrentUser
                            comment.Likes++
                            flag = false
                        }
                    })
                    while(flag) Thread.sleep(100)
                    likes.text = like.toString()
                    flag = true
                }
            }
            if(comment.Created != "-1") child.setOnLongClickListener{
                val builder1: AlertDialog.Builder = AlertDialog.Builder(this)
                if (comment.UserId != currentUserId)
                {
                    builder1.setMessage("Вы хотите ответить на комментарий?")
                    builder1.setCancelable(true)
                    builder1.setPositiveButton("Ответить") {dialog, _ ->
                        nowAnswers = true
                        var text = ""
                        sendComment.text = "Ответить"
                        var textview : EditText = findViewById(R.id.commentEnterText)
                        textview.isFocusableInTouchMode = true
                        textview.isFocusable = true
                        textview.requestFocus()
                        val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                        val inputMethodManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.showSoftInput(textview, InputMethodManager.SHOW_IMPLICIT)
                        sendComment.setOnClickListener {
                            textview = findViewById(R.id.commentEnterText)
                            val url = "http://185.119.56.91/api/Poems/SetReplyToComment?commentId=${comment.CommentId}&userId=$currentUserId&text=Ответ для ${comment.UserName}:\n ${textview.text}"
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
                                    text = "Комментарий отправлен"
                                    nowAnswers = false
                                }
                            })
                            while (text == "") Thread.sleep(100)
                            if(text == "Комментарий отправлен") sendComment.text = "Отправить"
                            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                            dialog.cancel()
                            imm.hideSoftInputFromWindow(textview.windowToken, 0)
                            finish()
                        }
                    }
                }
                else
                {
                    builder1.setMessage("Вы хотите удалить комментарий или ответить на него?")
                    builder1.setCancelable(true)
                    builder1.setNegativeButton("Удалить") { dialog, _ ->
                        var text = ""
                        var responsePost = ""
                        val url = "http://185.119.56.91/api/Poems/RemoveCommentFromPoem?commentId=${comment.CommentId}"
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
                                responsePost = response.body?.string().toString()
                                text = "Комментарий удален"
                            }
                        })
                        while (responsePost == "") Thread.sleep(100)
                        if(responsePost == "Ответ") linearLayout.removeView(it)
                        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                        finish()
                        dialog.cancel()
                    }
                    builder1.setPositiveButton("Ответить") {dialog, _ ->
                        nowAnswers = true
                        var text = ""
                        sendComment.text = "Ответить"
                        var textview : EditText = findViewById(R.id.commentEnterText)
                        textview.isFocusableInTouchMode = true
                        textview.isFocusable = true
                        textview.requestFocus()
                        val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                        val inputMethodManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.showSoftInput(textview, InputMethodManager.SHOW_IMPLICIT)
                        sendComment.setOnClickListener {
                            textview = findViewById(R.id.commentEnterText)
                            val url = "http://185.119.56.91/api/Poems/SetReplyToComment?commentId=${comment.CommentId}&userId=$currentUserId&text=Ответ для ${comment.UserName}:\n ${textview.text}"
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
                                    text = "Комментарий отправлен"
                                    nowAnswers = false
                                }
                            })
                            while (text == "") Thread.sleep(100)
                            if(text == "Комментарий отправлен") sendComment.text = "Отправить"
                            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                            dialog.cancel()
                            imm.hideSoftInputFromWindow(textview.windowToken, 0)
                            finish()
                        }
                    }
                }
                builder1.setNeutralButton("Отмена") { dialog, _ ->
                    dialog.cancel()
                    nowAnswers = false
                    sendComment.text = "Отправить"
                }
                val alert11: AlertDialog = builder1.create()
                alert11.show()
                nowAnswers = false
                sendComment.text = "Отправить"
                return@setOnLongClickListener true
            }
            if (comment.UpReplyId != null) {
                child.setPadding(50, 0, 0, 0)
            }
            linearLayout.addView(child)
        }
        if(!nowAnswers) sendComment.setOnClickListener {
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