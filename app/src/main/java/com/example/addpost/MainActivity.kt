package com.example.addpost

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.addpost.data.DummyData.posts
import com.example.addpost.data.RetrofitInstance
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.util.*

class MainActivity : AppCompatActivity() {


    //Declare postAdapter as a class-level variable
    private lateinit var postAdapter: PostAdapter

    //Variable for filtered posts (filtering posts using SearchView)
    private var filteredPosts: List<Post> = posts

    //Initialize retrofit from DummyData.kt
    private val api = RetrofitInstance.apiService

    //Variables to keep track of the current offset and limit
    var currentOffset = 0
    val limit = 10

    //When app starts or restarts, posts are cleared in case of duplication, then new posts fetched.
    override fun onStart() {
        super.onStart()
        posts.clear() //Posts are getting duplicated.. so we clear them and fetch again.
        currentOffset = 0
        fetchPosts()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Set toolbar title
        supportActionBar?.title = "Post 📭 App"

        //Tapping a post will navigate to post_activity and send post values as extras
        postAdapter = PostAdapter(posts) { post ->
            // handle post click here, e.g. navigate to a post page
            val intent = Intent(this, PostActivity::class.java)
            intent.putExtra("post", post)
            startActivity(intent)
        }

        //When load more button pressed, load 10 more posts
        postAdapter.loadMoreCallback = {
            fetchPosts()
        }

        //Initialization of SearchView and its input listener
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterPosts(newText)
                return true
            }
        })

        //Clicking on floating add button will navigate to post_form_activity
        val fabAddPost = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        navigateToPostForm(fabAddPost)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = postAdapter
    }

    //Fetch posts function
    private fun fetchPosts() {
        api.getPosts(limit, currentOffset).enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                val statusCode = response.code()
                if (statusCode == 200) {
                    Log.d("TAG", "FETCHED POSTS!!!")
                    val allPosts = response.body()
                    if (allPosts != null) {
                        currentOffset += limit
                        for (post in allPosts) {
                            posts.add(
                                Post(
                                    post.id,
                                    post.title,
                                    post.description,
                                    post.author,
                                    post.createdAt
                                )
                            )
                        }
                        //Notify any observers that the data set has changed
                        postAdapter.notifyDataSetChanged()
                    }

                } else {
                    Log.d("TAG", "Could not fetch the posts from the server.")
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.d("TAG", "Something went wrong...${t.message}")
            }
        })
    }

    //Filter through posts function
    private fun filterPosts(query: String?) {
        filteredPosts = if (query.isNullOrEmpty()) {
            posts // return all posts if search input field is empty
        } else {
            val lowerCaseQuery = query.lowercase(Locale.getDefault())
            posts.filter { post ->
                post.title.lowercase(Locale.getDefault())
                    .contains(lowerCaseQuery) || post.description.lowercase(Locale.getDefault())
                    .contains(lowerCaseQuery) || post.author.lowercase(Locale.getDefault())
                    .contains(lowerCaseQuery) || post.createdAt.lowercase(Locale.getDefault())
                    .contains(lowerCaseQuery)
            }
        }
        postAdapter.updatePosts(filteredPosts)
    }

    private fun navigateToPostForm(button: FloatingActionButton) {
        button.setOnClickListener {
            val intent = Intent(this, PostFormActivity::class.java)
            startActivity(intent)
        }
    }
}