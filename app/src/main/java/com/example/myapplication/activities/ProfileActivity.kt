package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.myapplication.R
import com.example.myapplication.dataModels.PoemsModel
import com.example.myapplication.dataModels.UserModel
import com.example.myapplication.services.APISender
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException


class ProfileActivity : AppCompatActivity() {
    private var _list : ArrayList<PoemsModel> = arrayListOf()
    private var _user : UserModel = UserModel("", "", arrayListOf(), arrayListOf(), false, arrayListOf(), arrayListOf(), "")
    private lateinit var createButton : ImageButton
    private lateinit var homeButton : ImageButton
    private lateinit var profileButton : ImageButton
    private lateinit var settingsButton : ImageButton
    private lateinit var subsButton : ImageButton
    private lateinit var notifyButton : ImageButton
    private lateinit var avatar : ImageView
    private lateinit var posts : TextView
    private lateinit var liked : TextView
    private lateinit var viewed : TextView
    private val selectPicture = 1
    lateinit var currentUserId : String
    lateinit var userId : String
    @SuppressLint("SetTextI18n", "InflateParams", "MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val json = jacksonObjectMapper()
        val api = APISender()
        currentUserId = intent.getStringExtra("currentUserId").toString()
        userId = intent.getStringExtra("userId").toString()
        _user = json.readValue(api.get("http://185.119.56.91/api/User/GetUserById?currentUserId=$currentUserId&userId=$userId"))
        _list = _user.Poems!!
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_layout)
        homeButton = findViewById(R.id.homeButton)
        createButton = findViewById(R.id.createButton)
        profileButton = findViewById(R.id.profileButton)
        settingsButton = findViewById(R.id.buttonSettings)
        subsButton = findViewById(R.id.subscribersButton)
        avatar = findViewById(R.id.avatar)
        posts = findViewById(R.id.posts)
        liked = findViewById(R.id.liked)
        viewed = findViewById(R.id.viewed)
        var typeFace: Typeface? = ResourcesCompat.getFont(applicationContext, R.font.montserrat)
        viewed.typeface = typeFace
        liked.typeface = typeFace
        typeFace = ResourcesCompat.getFont(applicationContext, R.font.bold)
        posts.typeface = typeFace
        val linearLayout: LinearLayout = findViewById(R.id.linearLayoutProfile)
        var child: View
        val subscribe: TextView = findViewById(R.id.subscribe)
        subscribe.text = if (_user.isSubscribedByCurrentUser) "Отписаться" else "Подписаться"
        val nick = findViewById<TextView>(R.id.profileNickName)
        println(nick.text)
        val likes = findViewById<TextView>(R.id.profileCountLikes)
        val countPoems = findViewById<TextView>(R.id.profileCountPoems)
        val countSubs = findViewById<TextView>(R.id.profileCountSubscribers)
        val countViews = findViewById<TextView>(R.id.profileCountViews)
        var wholeLikes = 0
        var wholeViews = 0
        if(_user.Photo != null) {
            val decodedByte: ByteArray = Base64.decode(_user.Photo, 0)
            val b = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
            avatar.setImageBitmap(Bitmap.createScaledBitmap(b, b.width/b.height * 100, 100, false))
        }
        nick.text = _user.NickName
        countPoems.text = "Публикации: ${_list.size}"
        countSubs.text = "Подписчики: ${_user.Subscribers.size}"
        for (i in 0 until _list.size) {
            val poem = _list[i]
            wholeLikes += poem.Likes
            wholeViews += poem.Views
            child = layoutInflater.inflate(R.layout.profile_layout_poemitem, null)
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
            if(userId == currentUserId) title.setOnLongClickListener {
                val builder1: AlertDialog.Builder = AlertDialog.Builder(this)
                builder1.setMessage("Вы хотите удалить или отредактировать пост?")
                builder1.setCancelable(true)
                builder1.setNegativeButton("Удалить") { dialog, _ ->
                    val text : String = if(api.post("http://185.119.56.91/api/Poems/DeletePoem?poemId=${poem.PoemId}", "")) "Пост удален"
                    else "Что-то пошло не так"
                    if (text != "Что-то пошло не так") linearLayout.removeView(it)
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                    dialog.cancel()
                }
                builder1.setPositiveButton("Отредактировать") { dialog, _ ->
                    val intent = Intent(this, UpdateActivity::class.java)
                    intent.putExtra("poemId", poem.PoemId)
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
        likes.text = "Лайки: $wholeLikes"
        countViews.text = "Просмотры: $wholeViews"
        subscribe.setOnClickListener {
            _user.isSubscribedByCurrentUser = api.post("http://185.119.56.91/api/User/SubscribeToUser?userId=${_user.Id}&currentUserId=$currentUserId", "")
            subscribe.text = if (_user.isSubscribedByCurrentUser) "Отписаться" else "Подписаться"
        }
        notifyButton = findViewById(R.id.notifyButton)
        notifyButton.setOnClickListener {
            Toast.makeText(applicationContext, "Уведомления пока недоступны. Следите за обновлениями!", Toast.LENGTH_LONG).show()
        }
        subsButton.setOnClickListener {
            val intent = Intent(this, FinderActivity::class.java)
            intent.putExtra("currentUserId", currentUserId)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finishAffinity()
        }
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra("currentUserId", currentUserId)
            intent.putExtra("userId", currentUserId)
            startActivity(intent)
            finish()
        }
        avatar.setOnClickListener{
            if(userId == currentUserId) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(
                        intent,
                        "Select Picture"
                    ), selectPicture
                )
            }
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
        posts.setOnClickListener {
            typeFace = ResourcesCompat.getFont(applicationContext, R.font.bold)
            posts.typeface = typeFace
            typeFace = ResourcesCompat.getFont(applicationContext, R.font.montserrat)
            viewed.typeface = typeFace
            liked.typeface = typeFace
            _list = _user.Poems!!
            linearLayout.removeAllViews()
            for (i in 0 until _list.size) {
                val poem = _list[i]
                child = layoutInflater.inflate(R.layout.profile_layout_poemitem, null)
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
                if(userId == currentUserId) title.setOnLongClickListener {
                    val builder1: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder1.setMessage("Вы хотите удалить или отредактировать пост?")
                    builder1.setCancelable(true)
                    builder1.setNegativeButton("Удалить") { dialog, _ ->
                        val text  : String = if(api.post("http://185.119.56.91/api/Poems/DeletePoem?poemId=${poem.PoemId}", "")) {
                            "Пост удален"
                        } else {
                            "Что-то пошло не так"
                        }
                        if (text != "Что-то пошло не так") linearLayout.removeView(it)
                        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                        dialog.cancel()
                    }
                    builder1.setPositiveButton("Отредактировать") { dialog, _ ->
                        val intent = Intent(this, UpdateActivity::class.java)
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
        }
        viewed.setOnClickListener {
            typeFace = ResourcesCompat.getFont(applicationContext, R.font.bold)
            viewed.typeface = typeFace
            typeFace = ResourcesCompat.getFont(applicationContext, R.font.montserrat)
            posts.typeface = typeFace
            liked.typeface = typeFace
            _list = _user.ViewedPoems!!
            linearLayout.removeAllViews()
            for (i in 0 until _list.size) {
                val poem = _list[i]
                child = layoutInflater.inflate(R.layout.profile_layout_poemitem, null)
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
                linearLayout.addView(child)
            }
        }
        liked.setOnClickListener {
            typeFace = ResourcesCompat.getFont(applicationContext, R.font.bold)
            liked.typeface = typeFace
            typeFace = ResourcesCompat.getFont(applicationContext, R.font.montserrat)
            viewed.typeface = typeFace
            posts.typeface = typeFace
            _list = _user.LikedPoems!!
            linearLayout.removeAllViews()
            for (i in 0 until _list.size) {
                val poem = _list[i]
                child = layoutInflater.inflate(R.layout.profile_layout_poemitem, null)
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
                linearLayout.addView(child)
            }
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == selectPicture && resultCode == RESULT_OK) {
            val selectedImage: Uri? = data?.data
            val bitmap: Bitmap?
            val api = APISender()
            try {
                val b = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                bitmap = Bitmap.createScaledBitmap(b, 100, 100, false)
                val bos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 10, bos)
                val bArray: ByteArray = bos.toByteArray()
                val imageEncoded: String = Base64.encodeToString(bArray, Base64.DEFAULT)
                avatar.setImageBitmap(bitmap)
                api.post("http://185.119.56.91/api/User/SetAvatarToUser?currentUserId=$userId", imageEncoded)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}