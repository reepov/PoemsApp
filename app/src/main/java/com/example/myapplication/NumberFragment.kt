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
            commentButton = requireView().findViewById(R.id.comment)
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
                flag = !flag;
            }
            commentButton.setOnClickListener{
                val intent = Intent(context, Comment::class.java)
                startActivity(intent)
            }
        }
    }
}