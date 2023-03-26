package com.example.addpost.data

import com.example.addpost.CreatePostResponse
import com.example.addpost.Post
import com.example.addpost.PostData
import com.example.addpost.data.DummyData.posts
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

object RetrofitInstance {
    //Initialize Retrofit with baseURL of the server
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.1.95:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //Api service to handle different types of requests
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    //interface definition for fetching data
    interface ApiService {
        //Get limit amount of posts from the server
        @GET("api/posts")
        fun getPosts(@Query("limit") limit: Int, @Query("offset") offset: Int): Call<List<Post>>

        //Post a single post to the server
        @POST("api/posts")
        fun createPost(@Body postData: PostData): Call<CreatePostResponse>

        //Edit a specific post using its post id
        @PUT("api/posts/{postId}")
        fun updatePost(
            @Path("postId") postId: String,
            @Body postData: PostData
        ): Call<CreatePostResponse>

        //Delete a specific post using its post id
        @DELETE("api/posts/{postId}")
        fun deletePost(
            @Path("postId") postId: String
        ): Call<CreatePostResponse>
    }
}

object DummyData {

    //Dummy data to hold all the posts (for the RecyclerView)
    var posts = mutableListOf<Post>(
        //Placeholder post when server is not connected
        Post("hello", "hello", "hello", "hello", "hello")
    )
}