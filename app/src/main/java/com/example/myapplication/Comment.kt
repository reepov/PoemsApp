package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PackageManagerCompat.LOG_TAG
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import java.time.LocalDate


class Comment : AppCompatActivity(){
    @RequiresApi(Build.VERSION_CODES.O)
    var comments : ArrayList<CommentModel> = arrayListOf(
        CommentModel("Елена Ивановна", 0, LocalDate.now().toString(), "Отличное стихотворение! Мне очень понравилось! Чувствуется рука мастера!", false),
        CommentModel("Слава Бразилии", 0, LocalDate.now().toString(), "Фу, сразу видно какой-то душевно больной писал этот кошмар.", false),
        CommentModel("Наркоман999", 0, LocalDate.now().toString(), "Мдаа, потраченное время жаль, конечно, но ладно", false),
        CommentModel("ivanzolo2004", 0, LocalDate.now().toString(), "Первый можно медальку?", false),
        CommentModel("brawl_leon2011", 0, LocalDate.now().toString(), "хз я лучше стихи писал когда разговаривать не умел еще", false),
        CommentModel("mne_nichego_ne_nravitsa", 0, LocalDate.now().toString(), "Кринж", false),
        CommentModel("check_zakrep", 0, LocalDate.now().toString(), "Шk0льHiцы ищи в tелеge skdjfh76", false),
        CommentModel("Мама", 0, LocalDate.now().toString(), "Супер! Так держать!", false)
    )
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comment_layout)
        val linearLayout : LinearLayout = findViewById(R.id.linearLayoutComment)
        val sendComment : Button = findViewById(R.id.sendComment)
        var child: View
        for (i in 0 until comments.size)
        {
            val comment = comments[i]
            child = layoutInflater.inflate(R.layout.comment_layout_item, null)
            val user = child.findViewById<TextView>(R.id.userName)
            user.text = comment.userName
            val textComment = child.findViewById<TextView>(R.id.commentText)
            textComment.text = comment.textComment
            val date = child.findViewById<TextView>(R.id.dateTime)
            date.text = comment.dateTime
            val image = child.findViewById<ImageView>(R.id.avatar)
            image.setImageResource(R.mipmap.ic_launcher)
            val likeButton = child.findViewById<ImageButton>(R.id.commentLike)
            val likes : TextView = child.findViewById(R.id.countCommLikes)
            likes.text = comment.countLikes.toString()
            var flag = true
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
                flag = !flag
            }
            linearLayout.addView(child)
        }
        sendComment.setOnClickListener {
            var child1 = layoutInflater.inflate(R.layout.comment_layout_item, null)
            val textview : EditText = findViewById(R.id.commentEnterText)
            val comment : CommentModel
            if (textview.text.toString() != "") {
                val date = LocalDate.now().toString()
                comment = CommentModel("admin", 0, date, textview.text.toString(), false)
                child1 = layoutInflater.inflate(R.layout.comment_layout_item, null)
                val user = child1.findViewById<TextView>(R.id.userName)
                user.text = comment.userName
                val textComment = child1.findViewById<TextView>(R.id.commentText)
                textComment.text = comment.textComment
                val dateView = child1.findViewById<TextView>(R.id.dateTime)
                dateView.text = comment.dateTime
                val image = child1.findViewById<ImageView>(R.id.avatar)
                image.setImageResource(R.mipmap.ic_launcher)
                comments.add(comment)
                val likeButton = child1.findViewById<ImageButton>(R.id.commentLike)
                val likes : TextView = child1.findViewById(R.id.countCommLikes)
                likes.text = comment.countLikes.toString()
                var flag = true
                likeButton.setOnClickListener{
                    if (!flag){
                        val like = (likes.text as String).toInt() - 1
                        likeButton.setImageResource(R.drawable.ic_like_before)
                        likes.text = like.toString()
                        comment.isLiked = false
                    }
                    else
                    {
                        val like = (likes.text as String).toInt() + 1
                        likeButton.setImageResource(R.drawable.ic_like_after);
                        likes.text = like.toString()
                        comment.isLiked = true
                    }
                    flag = !flag
                }
            }
            linearLayout.addView(child1)
            textview.text.clear()
        }
    }
}