package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.dataModels.PoemsModel
import com.example.myapplication.dataModels.UserModel
import com.example.myapplication.services.APISender
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.util.ArrayList

class FinderActivity : AppCompatActivity() {
    private lateinit var createButton : ImageButton
    private lateinit var homeButton : ImageButton
    private lateinit var profileButton : ImageButton
    private lateinit var subsButton : ImageButton
    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.finder_layout)
        val searchText = findViewById<EditText>(R.id.searchText)
        val publishLayout = findViewById<LinearLayout>(R.id.layoutPubs)
        val authorLayout = findViewById<LinearLayout>(R.id.layoutAuthors)
        val currentUserId = intent.getStringExtra("currentUserId")!!
        var resultPoems: ArrayList<PoemsModel>
        var resultAuthors : ArrayList<UserModel>
        val searchButton = findViewById<Button>(R.id.search)
        homeButton = findViewById(R.id.homeButton)
        createButton = findViewById(R.id.createButton)
        profileButton = findViewById(R.id.profileButton)
        subsButton = findViewById(R.id.subscribersButton)
        val api = APISender()
        val json = jacksonObjectMapper()
        resultPoems = json.readValue(api.get("http://185.119.56.91/api/Poems/ResultOfSearch?userId=$currentUserId&searchText="))
        resultAuthors = json.readValue(api.get("http://185.119.56.91/api/User/ResultOfUserSearch?userId=$currentUserId&searchText="))
        publishLayout.removeAllViews()
        authorLayout.removeAllViews()
        resultPoems.forEach{
            val poem = it
            val child = layoutInflater.inflate(R.layout.profile_layout_poemitem, null)
            val views = child.findViewById<TextView>(R.id.countViews)
            val title = child.findViewById<TextView>(R.id.poemTitle)
            title.text = title.text.toString() + poem.Title
            views.text = poem.Views.toString()
            child.setOnClickListener {
                val intent = Intent(applicationContext, PoemActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.putExtra("poemId", poem.PoemId)
                intent.putExtra("currentUserId", currentUserId)
                startActivity(intent)
            }
            publishLayout.addView(child)
        }
        resultAuthors.forEach{
            val user = it
            val child = layoutInflater.inflate(R.layout.profile_item, null)
            val avatar = child.findViewById<ImageView>(R.id.photo)
            val nickname = child.findViewById<TextView>(R.id.nickname)
            if(user.Photo != null) {
                val decodedByte: ByteArray = Base64.decode(user.Photo, 0)
                val b = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
                avatar.setImageBitmap(Bitmap.createScaledBitmap(b, b.width/b.height * 100, 100, false))
            }
            nickname.text = user.NickName
            child.setOnClickListener {
                val intent = Intent(this, ProfileActivity::class.java)
                println(currentUserId)
                intent.putExtra("currentUserId", currentUserId)
                intent.putExtra("userId", user.Id)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
            authorLayout.addView(child)
        }
        searchButton.setOnClickListener {
            resultPoems = json.readValue(api.get("http://185.119.56.91/api/Poems/ResultOfSearch?userId=$currentUserId&searchText=${searchText.text}"))
            publishLayout.removeAllViews()
            resultPoems.forEach{
                val poem = it
                val child = layoutInflater.inflate(R.layout.profile_layout_poemitem, null)
                val views = child.findViewById<TextView>(R.id.countViews)
                val title = child.findViewById<TextView>(R.id.poemTitle)
                title.text = title.text.toString() + poem.Title
                views.text = poem.Views.toString()
                title.setOnClickListener {
                    val intent = Intent(applicationContext, PoemActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intent.putExtra("poemId", poem.PoemId)
                    intent.putExtra("currentUserId", currentUserId)
                    startActivity(intent)
                }
                publishLayout.addView(child)
            }
            resultAuthors = json.readValue(api.get("http://185.119.56.91/api/User/ResultOfUserSearch?userId=$currentUserId&searchText=${searchText.text}"))
            authorLayout.removeAllViews()
            resultAuthors.forEach{
                val user = it
                val child = layoutInflater.inflate(R.layout.profile_item, null)
                val avatar = child.findViewById<ImageView>(R.id.photo)
                val nickname = child.findViewById<TextView>(R.id.nickname)
                if(user.Photo != null) {
                    val decodedByte: ByteArray = Base64.decode(user.Photo, 0)
                    val b = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
                    avatar.setImageBitmap(Bitmap.createScaledBitmap(b, b.width/b.height * 100, 100, false))
                }
                nickname.text = user.NickName
                child.setOnClickListener {
                    val intent = Intent(this, ProfileActivity::class.java)
                    println(currentUserId)
                    intent.putExtra("currentUserId", currentUserId)
                    intent.putExtra("userId", user.Id)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                }
                authorLayout.addView(child)
            }
        }
        subsButton.setOnClickListener {
            val intent = Intent(this, FinderActivity::class.java)
            intent.putExtra("currentUserId", currentUserId)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra("currentUserId", currentUserId)
            intent.putExtra("userId", currentUserId)
            startActivity(intent)
            finish()
        }
        createButton.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra("currentUserId", currentUserId)
            startActivity(intent)
            finish()
        }
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finishAffinity()
        }
    }
}