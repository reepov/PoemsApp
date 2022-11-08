package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.*
import java.io.IOException

class MainActivity : FragmentActivity() {

    private lateinit var adapter: NumberAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var createButton : ImageButton
    var List : ArrayList<PoemsModel> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://185.119.56.91/api/Poems/GetListOfRandomPoems")
            .build()
        var responseGet : String = ""
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("error")
            }
            override fun onResponse(call: Call, response: Response) {
                responseGet = response?.body?.string().toString()
                val JSON = jacksonObjectMapper()
                List = JSON.readValue<ArrayList<PoemsModel>>(responseGet)
                set(List)
            }
        })
        while (List.isEmpty()) continue
        adapter = NumberAdapter(this)
        viewPager = findViewById(R.id.pager)
        viewPager.adapter = adapter
        adapter.List = List
        createButton = findViewById(R.id.button18)
        createButton.setOnClickListener{
            val intent = Intent(this, Create::class.java)
            startActivity(intent)
        }
    }
    fun set(list : ArrayList<PoemsModel>)
    {
        List = list
    }
}