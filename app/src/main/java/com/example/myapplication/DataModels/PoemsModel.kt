package com.example.myapplication.DataModels

import java.io.Serializable

data class PoemsModel
    (val PoemId : String,
     val Title : String,
     val Text : String,
     var Likes : Int,
     val Views : Int,
     val isViewedByCurrentUser : Boolean,
     var isLikedByCurrentUser : Boolean,
     val CommentIds : List<String>?,
     val AuthorId : String) : Serializable