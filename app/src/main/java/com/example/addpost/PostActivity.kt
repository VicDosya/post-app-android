package com.example.addpost

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import com.example.addpost.data.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostActivity : AppCompatActivity() {

    //Initialize retrofit from DummyData.kt
    private val api = RetrofitInstance.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        //Add a BACK button in the toolbar
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        //Extract data extras passed from the main_activity
        //I need to use Parcelable here! but it is too much code for now.
        val post = intent.getSerializableExtra("post") as Post
        val postId = post.id
        val title = post.title
        val description = post.description
        val author = post.author
        val date = post.createdAt

        // Set TextViews with post details
        findViewById<TextView>(R.id.tvTitle).text = title
        findViewById<TextView>(R.id.tvDescription).text = description
        findViewById<TextView>(R.id.tvAuthor).text = author
        findViewById<TextView>(R.id.tvDate).text = date

        //Grab title extra and set it as a toolbar title
        supportActionBar?.title = title

        //Edit Button functionality with Edit intention boolean
        val editButton = findViewById<Button>(R.id.editButton)
        editButton.setOnClickListener {
            val intent = Intent(this, PostFormActivity::class.java)
            intent.putExtra("post", post)
            intent.putExtra("editMode", true)
            startActivity(intent)
        }

        //Delete button functionality to delete a specific post.
        val deleteButton = findViewById<Button>(R.id.deleteButton)
        deletePost(deleteButton, postId)


        //PIZZA BUTTON ANIMATION CODE
        //Get the pizza button view
        val button = findViewById<Button>(R.id.pizza_button)
        animatePizza(button)
    }

    //Function to run the BACK button in the toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun animatePizza(button: Button) {
        button.setOnClickListener {
            //Create rotation animation
            val rotation = ObjectAnimator.ofFloat(button, "rotation", 0f, 360f)
            rotation.duration = 500 // rotation animation duration
            rotation.interpolator = AccelerateDecelerateInterpolator()

            // Create the scale animation
            val scaleUpX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.5f)
            val scaleUpY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.5f)
            val scaleDownX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.5f, 1f)
            val scaleDownY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.5f, 1f)
            val scale = ObjectAnimator.ofPropertyValuesHolder(
                button, scaleUpX, scaleUpY, scaleDownX, scaleDownY
            )
            scale.duration = 500 // adjust the animation duration as needed
            scale.startDelay = 100 // adjust the delay time as needed

            //Create the fade-in animation
            val fadeIn = ObjectAnimator.ofFloat(button, "alpha", 1f, 0.5f)
            fadeIn.duration = 200 // fade in animation duration

            //Create the fade-out animation
            val fadeOut = ObjectAnimator.ofFloat(button, "alpha", 0.5f, 1f)
            fadeOut.startDelay = 300 // delay time
            fadeOut.duration = 200 // fadeout animation duration

            //Combine all the animations into an AnimatorSet
            val animatorSet = AnimatorSet()
            animatorSet.play(rotation).with(scale).with(fadeIn).before(fadeOut)
            animatorSet.start()
        }
    }

    private fun deletePost(button: Button, postId: String) {
        button.setOnClickListener {
            if (postId != null) {
                api.deletePost(postId).enqueue(object : Callback<CreatePostResponse> {
                    override fun onResponse(
                        call: Call<CreatePostResponse>, response: Response<CreatePostResponse>
                    ) {
                        val statusCode = response.code()
                        if (statusCode == 200) {
                            Log.d("TAG", "${response.body()}")
                        } else {
                            Log.d("TAG", "Failed to delete a post...")
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
    }
}