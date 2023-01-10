package com.example.myapplication.services

import okhttp3.*
import okhttp3.internal.EMPTY_REQUEST
import java.io.IOException

class APISender {
    fun get(url : String): String
    {
        val client = OkHttpClient()
        var bool = true
        val request = Request.Builder()
            .url(url)
            .build()
        var responseGet = ""
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error: " + e.message)
            }
            override fun onResponse(call: Call, response: Response) {
                responseGet = response.body?.string().toString()
                if(response.code == 200)
                {
                    bool = false
                    response.close()
                }
            }
        })
        while(bool) Thread.sleep(100)
        return responseGet
    }
    fun post(url: String, formData: String) : Boolean
    {
        val client: OkHttpClient = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .build()
        var bool = true
        var result = false
        val requestBody: RequestBody = if(formData != "") MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("formData", formData)
            .build() else EMPTY_REQUEST
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error: " + e.message)
                result = false
                bool = false
            }
            override fun onResponse(call: Call, response: Response) {
                if(response.code == 200) {
                    bool = false
                    result = response.body?.string().toString() == "true"
                    response.close()
                }
            }
        })
        while(bool) Thread.sleep(100)
        return result
    }
}