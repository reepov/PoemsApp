package com.example.myapplication.dataModels

import java.io.Serializable

class UserModel(
    val Id : String,
    val NickName : String,
    val Poems : ArrayList<PoemsModel>,
    val Subscribers : ArrayList<String>,
    var isSubscribedByCurrentUser : Boolean,
    val LikedPoems : ArrayList<PoemsModel>,
    val ViewedPoems : ArrayList<PoemsModel>,
    val Photo : String?
) : Serializable