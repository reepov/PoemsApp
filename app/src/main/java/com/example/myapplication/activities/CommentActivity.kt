package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Base64
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.myapplication.R
import com.example.myapplication.dataModels.CommentModel
import com.example.myapplication.services.APISender
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.*

class CommentActivity : AppCompatActivity(){
    private var list : ArrayList<CommentModel> = arrayListOf()

    @SuppressLint("InflateParams")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val poemId = intent.getStringExtra("poemId")
        val currentUserId = intent.getStringExtra("currentUserId")
        var nowAnswers = false
        val json = jacksonObjectMapper()
        val api = APISender()
        list = json.readValue(api.get("http://185.119.56.91/api/Poems/GetCommentsByPoemId?userId=$currentUserId&poemId=$poemId"))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comment_layout)
        val linearLayout : LinearLayout = findViewById(R.id.linearLayoutComment)
        val sendComment : Button = findViewById(R.id.sendComment)
        val textview : EditText = findViewById(R.id.commentEnterText)
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
                image.setImageBitmap(Bitmap.createScaledBitmap(b, b.width / b.height * 50, 50, false))
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
                    if(api.post("http://185.119.56.91/api/Poems/RemoveLikeFromComment?userId=$currentUserId&commentId=${comment.CommentId}", ""))
                    {
                        val like = (likes.text as String).toInt() - 1
                        likeButton.setImageResource(R.drawable.ic_before_dasha)
                        comment.isLikedByCurrentUser = !comment.isLikedByCurrentUser
                        comment.Likes--
                        likes.text = like.toString()
                    }
                    else Toast.makeText(applicationContext, "Произошла неизвестная ошибка", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    if(api.post("http://185.119.56.91/api/Poems/SetLikeToComment?userId=$currentUserId&commentId=${comment.CommentId}", ""))
                    {
                        val like = (likes.text as String).toInt() + 1
                        likeButton.setImageResource(R.drawable.ic_after_dasha)
                        comment.isLikedByCurrentUser = !comment.isLikedByCurrentUser
                        comment.Likes++
                        likes.text = like.toString()
                    }
                    else Toast.makeText(applicationContext, "Произошла неизвестная ошибка", Toast.LENGTH_SHORT).show()
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
                        var text : String
                        sendComment.text = "Ответить"
                        textview.isFocusableInTouchMode = true
                        textview.isFocusable = true
                        textview.requestFocus()
                        val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                        val inputMethodManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.showSoftInput(textview, InputMethodManager.SHOW_IMPLICIT)
                        textview.addTextChangedListener {
                            if(textview.text.length > 150) textview.setText(textview.text.substring(0, 150))
                            println(textview.text.length)
                        }
                        sendComment.setOnClickListener {
                            if(api.post("http://185.119.56.91/api/Poems/SetReplyToComment?commentId=${comment.CommentId}&userId=$currentUserId&text=Ответ для ${comment.UserName}:\n ${textview.text}", "")){
                                text = "Комментарий отправлен"
                                nowAnswers = false
                            }
                            else{
                                text = "Что-то пошло не так"
                            }
                            if(text == "Комментарий отправлен") sendComment.text = "Отправить"
                            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                            dialog.cancel()
                            imm.hideSoftInputFromWindow(textview.windowToken, 0)
                            val intents = Intent(applicationContext, CommentActivity::class.java)
                            intents.putExtra("poemId", poemId)
                            intents.putExtra("currentUserId", currentUserId)
                            intents.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            startActivity(intents)
                            finish()
                            overridePendingTransition(0, 0)
                        }
                    }
                }
                else
                {
                    builder1.setMessage("Вы хотите удалить комментарий или ответить на него?")
                    builder1.setCancelable(true)
                    builder1.setNegativeButton("Удалить") { dialog, _ ->
                        val text : String = if(api.post("http://185.119.56.91/api/Poems/RemoveCommentFromPoem?commentId=${comment.CommentId}", "")) {
                            "Комментарий удален"
                        } else {
                            "Комментарий удален"
                        }
                        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                        val intents = Intent(applicationContext, CommentActivity::class.java)
                        intents.putExtra("poemId", poemId)
                        intents.putExtra("currentUserId", currentUserId)
                        intents.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        startActivity(intents)
                        finish()
                        overridePendingTransition(0, 0)
                        dialog.cancel()
                    }
                    builder1.setPositiveButton("Ответить") {dialog, _ ->
                        nowAnswers = true
                        var text : String
                        sendComment.text = "Ответить"
                        textview.isFocusableInTouchMode = true
                        textview.isFocusable = true
                        textview.requestFocus()
                        val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                        val inputMethodManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.showSoftInput(textview, InputMethodManager.SHOW_IMPLICIT)
                        sendComment.setOnClickListener {
                            text = if(api.post("http://185.119.56.91/api/Poems/SetReplyToComment?commentId=${comment.CommentId}&userId=$currentUserId&text=Ответ для ${comment.UserName}:\n ${textview.text}", "")) {
                                "Комментарий отправлен"
                            } else {
                                "Что-то пошло не так"
                            }
                            if(text == "Комментарий отправлен") sendComment.text = "Отправить"
                            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                            dialog.cancel()
                            imm.hideSoftInputFromWindow(textview.windowToken, 0)
                            val intents = Intent(applicationContext, CommentActivity::class.java)
                            intents.putExtra("poemId", poemId)
                            intents.putExtra("currentUserId", currentUserId)
                            intents.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            startActivity(intents)
                            finish()
                            overridePendingTransition(0, 0)
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
            if (comment.UpReplyId != null) child.setPadding(50, 0, 0, 0)
            linearLayout.addView(child)
        }
        if(!nowAnswers) sendComment.setOnClickListener {

            if (textview.text.toString() != "" && api.post("http://185.119.56.91/api/Poems/SetCommentToPoem?userId=$currentUserId&poemId=$poemId&text=${textview.text}","")) {
                val intents = Intent(applicationContext, CommentActivity::class.java)
                intents.putExtra("poemId", poemId)
                intents.putExtra("currentUserId", currentUserId)
                intents.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intents)
                finish()
                overridePendingTransition(0, 0)
                Toast.makeText(this, "Комментарий отправлен", Toast.LENGTH_SHORT).show()
            }
            else Toast.makeText(this, "Произошла неизвестная ошибка", Toast.LENGTH_SHORT).show()
            textview.text.clear()
        }
        textview.addTextChangedListener {
            if(it?.length!! > 150) {
                textview.setText(it.substring(0, 150))
                textview.setSelection(it.length - 1)
                Toast.makeText(applicationContext, "Вы превысили лимит символов в комментарии - 150 символов.", Toast.LENGTH_SHORT).show()
            }
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