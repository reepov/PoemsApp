package com.example.myapplication.Services

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapplication.Activities.Comment
import com.example.myapplication.DataModels.PoemsModel
import com.example.myapplication.R
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            val textView: TextView = view.findViewById(R.id.textView)
            val title : TextView = view.findViewById(R.id.titleTextView)
            val likes : TextView = view.findViewById(R.id.countLikes)
            val comms : TextView = view.findViewById(R.id.countComms)
            textView.movementMethod = ScrollingMovementMethod()
            val poema = poem!!
            likes.text = poema.likes.toString()
            comms.text = if(poema.commentIds != null) poema.commentIds.size.toString() else "0"
            title.text = poema.title
            textView.text = poema.text
            likeButton = requireView().findViewById(R.id.like)
            if(poema.isLikedByCurrentUser) likeButton.setImageResource(R.drawable.ic_like_after)
            else likeButton.setImageResource(R.drawable.ic_like_before)
            commentButton = requireView().findViewById(R.id.comment)
            likeButton.setOnClickListener{
                if (poema.isLikedByCurrentUser){
                    val url = "http://185.119.56.91/api/Poems/RemoveLikeFromPoem?userId=$currentUserId&poemId=${poema.poemId}"
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
                            likeButton.setImageResource(R.drawable.ic_like_before_dasha)
                            likes.text = like.toString()
                            poema.isLikedByCurrentUser = !poema.isLikedByCurrentUser
                            poema.likes--
                        }
                    })

                }
                else
                {
                    val url = "http://185.119.56.91/api/Poems/SetLikeToPoem?userId=$currentUserId&poemId=${poema.poemId}"
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
                            likeButton.setImageResource(R.drawable.ic_like_after)
                            likes.text = like.toString()
                            poema.isLikedByCurrentUser = !poema.isLikedByCurrentUser
                            poema.likes++
                        }
                    })
                }
            }
            commentButton.setOnClickListener{
                val intent = Intent(context, Comment::class.java)
                intent.putExtra("poemId", poema.poemId)
                intent.putExtra("currentUserId", currentUserId)
                startActivity(intent)
            }
        }
    }
}