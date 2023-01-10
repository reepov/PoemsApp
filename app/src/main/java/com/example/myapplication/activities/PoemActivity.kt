package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.content.Context
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
import com.example.myapplication.services.APISender
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.*

class PoemActivity : AppCompatActivity() {
    var poema : PoemsModel? = null
    private lateinit var likeButton : ImageButton
    private lateinit var commentButton : ImageButton
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_number)
        val intent : Intent = intent
        val action : String? = intent.action
        val data: String? = intent.dataString
        val api = APISender()
        val json = jacksonObjectMapper()
        val poemId : String = if (Intent.ACTION_VIEW == action && data != null) {
            data.split("poemId=")[1]
        } else {
            intent.getStringExtra("poemId").toString()
        }
        val sharedPreferences = getSharedPreferences("USER_INFO_SP", Context.MODE_PRIVATE)
        val currentUserId = sharedPreferences.getString("CurrentUserId", null)
        if(currentUserId != null) poema = json.readValue<PoemsModel>(api.get("http://185.119.56.91/api/Poems/GetPoemById?userId=$currentUserId&poemId=$poemId"))
        else
        {
            val intents = Intent(this, LoginActivity::class.java)
            startActivity(intents)
            finishAffinity()
        }
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
                if(api.post("http://185.119.56.91/api/Poems/RemoveLikeFromPoem?userId=$currentUserId&poemId=${poema!!.PoemId}", ""))
                {
                    val like = (likes.text as String).toInt() - 1
                    likeButton.setImageResource(R.drawable.ic_before_dasha)
                    likes.text = like.toString()
                    poema!!.isLikedByCurrentUser = !poema!!.isLikedByCurrentUser
                    poema!!.Likes--
                }
            }
            else
            {
                if(api.post("http://185.119.56.91/api/Poems/SetLikeToPoem?userId=$currentUserId&poemId=${poema!!.PoemId}", ""))
                {
                    val like = (likes.text as String).toInt() + 1
                    likeButton.setImageResource(R.drawable.ic_after_dasha)
                    likes.text = like.toString()
                    poema!!.isLikedByCurrentUser = !poema!!.isLikedByCurrentUser
                    poema!!.Likes++
                }
            }
        }
        commentButton.setOnClickListener{
            val intents = Intent(applicationContext, CommentActivity::class.java)
            intents.putExtra("poemId", poema!!.PoemId)
            intents.putExtra("currentUserId", currentUserId)
            intents.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intents)
        }
        api.post("http://185.119.56.91/api/Poems/SetViewToPoem?userId=$currentUserId&poemId=${poema!!.PoemId}", "")
    }
}