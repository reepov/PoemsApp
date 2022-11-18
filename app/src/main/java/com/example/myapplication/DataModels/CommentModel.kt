package com.example.myapplication.DataModels

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class CommentModel(
    var CommentId : String,
    var UserName: String?,
    var Text: String?,
    var Likes: Int = 0,
    var isLikedByCurrentUser : Boolean,
    var Created : String?,
    var RepliesId : List<String>?,
    var UpReplyId : String?,
    var PoemId : String) : Serializable {
}