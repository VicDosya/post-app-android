package com.example.addpost

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.addpost.data.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostFormActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)

    //Initialize retrofit from DummyData
    val api = RetrofitInstance.apiService

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        //Add a BACK button in the toolbar
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)


        //Get all input EditText views
        val inputTitle = findViewById<EditText>(R.id.etTitleInput)
        val inputDescription = findViewById<EditText>(R.id.etDescriptionInput)
        val inputAuthor = findViewById<EditText>(R.id.etAuthorInput)
        //Submit button view
        val submitButton = findViewById<Button>(R.id.submitButton)
        //Status message to show errors
        val statusMessage = findViewById<TextView>(R.id.tvStatusMessage)

        //Editing mode condition - if true, the client is editing the post, else - otherwise.
        val editMode = intent.getBooleanExtra("editMode", false)

        if (editMode) {
            //Set toolbar title
            supportActionBar?.title = "Edit Post 📑"

            //Fill the input fields with post data extras (passed from the post_activity)
            val postId = intent.getStringExtra("postId")
            val title = intent.getStringExtra("title")
            val description = intent.getStringExtra("description")
            val author = intent.getStringExtra("author")
            inputTitle.setText(title)
            inputDescription.setText(description)
            inputAuthor.setText(author)

            submitButton.setOnClickListener {
                //API request to update the post
                if (postId != null) {
                    val postData = PostData(
                        inputTitle.text.toString(),
                        inputDescription.text.toString(),
                        inputAuthor.text.toString()
                    )

                    api.updatePost(postId, postData).enqueue(object : Callback<CreatePostResponse> {
                        override fun onResponse(
                            call: Call<CreatePostResponse>,
                            response: Response<CreatePostResponse>
                        ) {
                            val statusCode = response.code()
                            if (statusCode == 200) {
                                Log.d("TAG", "${response.body()}")

                            } else {
                                Log.d("TAG", "Failed to edit a post...")
                            }
                        }

                        override fun onFailure(call: Call<CreatePostResponse>, t: Throwable) {
                            Log.d("TAG", "Something went wrong... ${t.message}")
                        }
                    })

                    //Navigate back to home
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Log.d("TAG", "Post id is not found(?)")
                }

            }

        } else {
            //Editing mode is FALSE - user creates a new post
            //Set toolbar title
            supportActionBar?.title = "Create a new post 📃"

            submitButton.setOnClickListener {
                //If input fields are not empty, create a new post.
                if (inputTitle.text.toString().isNotEmpty() && inputDescription.text.toString()
                        .isNotEmpty() && inputAuthor.text.toString().isNotEmpty()
                ) {
                    //postData variable with input fields data
                    val postData = PostData(
                        inputTitle.text.toString(),
                        inputDescription.text.toString(),
                        inputAuthor.text.toString()
                    )

                    //Call the createPost API and send the postData input values
                    api.createPost(postData).enqueue(object : Callback<CreatePostResponse> {
                        override fun onResponse(
                            call: Call<CreatePostResponse>,
                            response: Response<CreatePostResponse>
                        ) {
                            val statusCode = response.code()
                            if (statusCode == 201) {
                                Log.d("TAG", "${response.body()}")
                            } else {
                                Log.d("TAG", "Failed to submit a post...")
                            }

                        }

                        override fun onFailure(call: Call<CreatePostResponse>, t: Throwable) {
                            Log.d("TAG", "Something went wrong... ${t.message}")
                        }
                    })


                    //Navigate back to home
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Log.d("TAG", "Input fields are empty, post not saved.")
                    statusMessage.text = getString(R.string.input_empty_warning)
                    if (inputTitle.text.isEmpty()) {
                        inputTitle.backgroundTintList =
                            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))
                    }
                    if (inputDescription.text.isEmpty()) {
                        inputDescription.backgroundTintList =
                            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))
                    }
                    if (inputAuthor.text.isEmpty()) {
                        inputAuthor.backgroundTintList =
                            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))
                    }

                }
            }
        }

    }

    //Function to run the BACK button in the toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}