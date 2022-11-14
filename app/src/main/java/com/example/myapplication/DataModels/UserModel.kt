package com.example.myapplication.DataModels

import com.example.myapplication.DataModels.PoemsModel
import java.io.Serializable

class UserModel(
    val id : String,
    val nickName : String,
    val poems : ArrayList<PoemsModel>
) : Serializable