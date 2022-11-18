package com.example.myapplication.Services

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication.Services.ARG_OBJECT
import com.example.myapplication.DataModels.PoemsModel
import com.example.myapplication.Services.NumberFragment

class NumberAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    var list : ArrayList<PoemsModel> = arrayListOf()
    var currentUserId = ""
    override fun getItemCount(): Int = list.size
    override fun createFragment(position: Int): Fragment {
        val fragment = NumberFragment(list[position], currentUserId)
        fragment.arguments = Bundle().apply {
            putInt(ARG_OBJECT, position + 1)
        }
        return fragment
    }
}