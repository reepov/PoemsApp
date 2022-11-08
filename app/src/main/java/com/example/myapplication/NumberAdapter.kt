package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class NumberAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {
    var List : ArrayList<PoemsModel> = arrayListOf()
    var index = 0
    override fun getItemCount(): Int = List.size
    override fun createFragment(position: Int): Fragment {

        val fragment = NumberFragment(List[index])
        if (index == List.size - 1) index = 0 else index++
        fragment.arguments = Bundle().apply {
            putInt(ARG_OBJECT, position + 1)
        }
        return fragment
    }
}