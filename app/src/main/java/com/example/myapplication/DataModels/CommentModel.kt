package com.example.myapplication.DataModels

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class CommentModel(
    var commentId : String,
    var userName: String?,
    var text: String?,
    var likes: Int = 0,
    var isLikedByCurrentUser : Boolean,
    var created : String?,
    var repliesId : List<String>?,
    var upReplyId : String?,
    var poemId : String) : Serializable {
}