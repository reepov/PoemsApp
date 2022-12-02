package com.example.myapplication.services

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapplication.activities.Comment
import com.example.myapplication.dataModels.PoemsModel
import com.example.myapplication.R
import com.example.myapplication.activities.Profile
import okhttp3.*
import okhttp3.internal.EMPTY_REQUEST
import java.io.IOException

const val ARG_OBJECT = "object"

class NumberFragment() : Fragment() {
    private var poem : PoemsModel? = null
    private var currentUserId : String = ""
    constructor(Poem : PoemsModel, CurrentUserId : String) : this() {
        poem = Poem
        currentUserId = CurrentUserId
    }
    private lateinit var likeButton : ImageButton
    private lateinit var commentButton : ImageButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_number, container, false)
    }
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            val textView: TextView = view.findViewById(R.id.textView)
            val title : TextView = view.findViewById(R.id.titleTextView)
            val likes : TextView = view.findViewById(R.id.countLikes)
            val comms : TextView = view.findViewById(R.id.countComms)
            val userLink : TextView = view.findViewById(R.id.userLink)
            val publish : TextView = view.findViewById(R.id.publishDate)
            val descript : TextView = view.findViewById(R.id.descriptionText)
            textView.movementMethod = ScrollingMovementMethod()
            val poema = poem!!
            userLink.text = poema.UserName
            descript.text = poema.Description ?: ""
            likes.text = poema.Likes.toString()
            comms.text = if(poema.CommentIds != null) poema.CommentIds.size.toString() else "0"
            title.text = poema.Title
            textView.text = poema.Text
            publish.text = "${publish.text} ${poema.Created}"
            likeButton = requireView().findViewById(R.id.like)
            if(poema.isLikedByCurrentUser) likeButton.setImageResource(R.drawable.ic_after_dasha)
            else likeButton.setImageResource(R.drawable.ic_before_dasha)
            commentButton = requireView().findViewById(R.id.comment)

            userLink.setOnClickListener {
                val intent = Intent(context, Profile::class.java)
                intent.putExtra("currentUserId", currentUserId)
                intent.putExtra("userId", poema.AuthorId)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
            }
            likeButton.setOnClickListener{
                if (poema.isLikedByCurrentUser){
                    val url = "http://185.119.56.91/api/Poems/RemoveLikeFromPoem?userId=$currentUserId&poemId=${poema.PoemId}"
                    val client = OkHttpClient()
                    val request = Request.Builder()
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
                            poema.isLikedByCurrentUser = !poema.isLikedByCurrentUser
                            poema.Likes--
                        }
                    })

                }
                else
                {
                    val url = "http://185.119.56.91/api/Poems/SetLikeToPoem?userId=$currentUserId&poemId=${poema.PoemId}"
                    val client = OkHttpClient()
                    val request = Request.Builder()
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
                            poema.isLikedByCurrentUser = !poema.isLikedByCurrentUser
                            poema.Likes++
                        }
                    })
                }
            }
            commentButton.setOnClickListener{
                val intent = Intent(context, Comment::class.java)
                intent.putExtra("poemId", poema.PoemId)
                intent.putExtra("currentUserId", currentUserId)
                startActivity(intent)
            }
            val url = "http://185.119.56.91/api/Poems/SetViewToPoem?userId=$currentUserId&poemId=${poema.PoemId}"
            val client = OkHttpClient()
            val request = Request.Builder()
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
}