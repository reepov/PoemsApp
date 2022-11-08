package com.example.myapplication

import java.io.Serializable

data class PoemsModel
    (val poemId : String,
     val title : String,
     val text : String,
     var likes : Int,
     val views : Int,
     val isViewedByCurrentUser : Boolean,
     var isLikedByCurrentUser : Boolean,
     val commentIds : List<String>?,
     val authorId : String) : Serializable