package com.example.addpost

import com.google.gson.annotations.SerializedName

//Post data class
data class Post(
    @SerializedName("_id") val id: String,
    val title: String,
    val description: String,
    val author: String,
    val createdAt: String,
)

//Post response from the server (e.g: "message: "success!")
data class CreatePostResponse(val message: String)

//Data type for POST and PUT requests
data class PostData(val title: String, val description: String, val author: String)
