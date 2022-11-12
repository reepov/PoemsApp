package com.example.myapplication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.*
import okhttp3.internal.EMPTY_REQUEST
import java.io.IOException

class Profile : AppCompatActivity() {
    var List : ArrayList<PoemsModel> = arrayListOf()
    var User : UserModel = UserModel("", "", arrayListOf())
    private lateinit var createButton : ImageButton
    private lateinit var homeButton : ImageButton
    private lateinit var profileButton : ImageButton
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val client = OkHttpClient()
        var bool = true
        val userId = intent.getStringExtra("userId")
        val request = Request.Builder()
            .url("http://185.119.56.91/api/User/GetUserById?currentUserId=$userId&userId=$userId")
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
                User = JSON.readValue<UserModel>(responseGet)
                if(response.code == 200) bool = false
            }
        })
        while(bool){
            Thread.sleep(100)
            continue
        }
        bool = true
        List = User.poems
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_layout)
        homeButton = findViewById(R.id.homeButton)
        createButton = findViewById(R.id.createButton)
        profileButton = findViewById(R.id.profileButton)
        profileButton.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            intent.putExtra("userId", "e4e60c56-f038-4a1a-89b9-70a4c869d8e0")
            startActivity(intent)
            finish()
        }
        createButton.setOnClickListener{
            val intent = Intent(this, Create::class.java)
            startActivity(intent)
            finish()
        }
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        var nick = findViewById<TextView>(R.id.profileNickName)
        println(nick.text)
        var likes = findViewById<TextView>(R.id.profileCountLikes)
        var countPoems = findViewById<TextView>(R.id.profileCountPoems)
        var wholeLikes = 0
        nick.text = User.nickName
        countPoems.text = "${List.size} posts"
        val linearLayout : LinearLayout = findViewById(R.id.linearLayoutProfile)
        var child: View
        for (i in 0 until List.size)
        {
            val poem = List[i]
            wholeLikes += poem.likes
            child = layoutInflater.inflate(R.layout.profile_layout_poemitem, null)
            //val user = child.findViewById<TextView>(R.id.userName)
            //user.text = comment.userName
            val title = child.findViewById<TextView>(R.id.poemTitle)
            title.text = title.text.toString() + poem.title
            title.setOnClickListener {
                val intent = Intent(applicationContext, Poems::class.java)
                intent.putExtra("poemId", poem.poemId)
                intent.putExtra("userId", "e4e60c56-f038-4a1a-89b9-70a4c869d8e0")
                startActivity(intent)
            }
            //val date = child.findViewById<TextView>(R.id.dateTime)
            //date.text = comment.created
            //val image = child.findViewById<ImageView>(R.id.avatar)
            //image.setImageResource(R.mipmap.ic_launcher)
            //val likeButton = child.findViewById<ImageButton>(R.id.commentLike)
            //val likes : TextView = child.findViewById(R.id.countCommLikes)
            //likes.text = comment.likes.toString()
            //var flag = true
            /*likeButton.setOnClickListener{
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
            }*/
            linearLayout.addView(child)
        }
        likes.text = "$wholeLikes likes"
        /*sendComment.setOnClickListener {
            val textview : EditText = findViewById(R.id.commentEnterText)
            if (textview.text.toString() != "") {
                var url = "http://185.119.56.91/api/Poems/SetCommentToPoem?userId=e4e60c56-f038-4a1a-89b9-70a4c869d8e0&poemId=$poemId&text=${textview.text}"
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
        }*/
    }
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null) {
            if (keyCode == KeyEvent.KEYCODE_BACK && !event.isCanceled()) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}