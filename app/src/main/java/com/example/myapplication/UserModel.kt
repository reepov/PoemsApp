package com.example.myapplication

import java.io.Serializable

class UserModel(
    val id : String,
    val nickName : String,
    val poems : ArrayList<PoemsModel>
) : Serializable