package com.example.myapplication

import java.io.Serializable

data class PoemsModel
    (val id : String,
     val title : String,
     val text : String,
     val likes : Int,
     val views : Int,
     val commentIds : String?,
     val authorId : String,
     val author : String?) : Serializable