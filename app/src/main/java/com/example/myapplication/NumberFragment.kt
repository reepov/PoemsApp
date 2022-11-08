package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import okhttp3.*
import okhttp3.internal.EMPTY_REQUEST
import java.io.IOException


const val ARG_OBJECT = "object"

class NumberFragment() : Fragment() {
    var poem : PoemsModel? = null
    constructor(Poem : PoemsModel) : this() {
        poem = Poem
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
            textView.movementMethod = ScrollingMovementMethod()
            val poema = poem!!
            likes.text = poema.likes.toString();
            title.text = poema.title
            textView.text = poema.text
            var flag = true
            likeButton = requireView().findViewById(R.id.like)
            if(poema.isLikedByCurrentUser) likeButton.setImageResource(R.drawable.ic_like_after)
            else likeButton.setImageResource(R.drawable.ic_like_before);
            commentButton = requireView().findViewById(R.id.comment)
            likeButton.setOnClickListener{
                if (poema.isLikedByCurrentUser){
                    val url = "http://185.119.56.91/api/Poems/RemoveLikeFromPoem?userId=e4e60c56-f038-4a1a-89b9-70a4c869d8e0&poemId=${poema.poemId}"
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
                            val like = (likes.text as String).toInt() - 1
                            likeButton.setImageResource(R.drawable.ic_like_before)
                            likes.text = like.toString()
                            poema.isLikedByCurrentUser = !poema.isLikedByCurrentUser
                            poema.likes--
                            //println("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" + response.code)
                        }
                    })

                }
                else
                {
                    var url = "http://185.119.56.91/api/Poems/SetLikeToPoem?userId=e4e60c56-f038-4a1a-89b9-70a4c869d8e0&poemId=${poema.poemId}"
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
                            val like = (likes.text as String).toInt() + 1
                            likeButton.setImageResource(R.drawable.ic_like_after);
                            likes.text = like.toString()
                            poema.isLikedByCurrentUser = !poema.isLikedByCurrentUser
                            poema.likes++
                            //println("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN" + response.code)
                        }
                    })
                }
            }
            commentButton.setOnClickListener{
                val intent = Intent(context, Comment::class.java)
                startActivity(intent)
            }
        }
    }
}