package com.example.myapplication

import android.os.Parcel
import android.os.Parcelable

class CommentModel(
    var userName: String?,
    var countLikes: Int = 0,
    var dateTime: String?,
    var textComment: String?,
    var isLiked : Boolean) {
}