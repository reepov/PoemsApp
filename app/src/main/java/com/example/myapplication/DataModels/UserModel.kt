package com.example.myapplication.DataModels

import com.example.myapplication.DataModels.PoemsModel
import java.io.Serializable

class UserModel(
    val Id : String,
    val NickName : String,
    val Poems : ArrayList<PoemsModel>
) : Serializable