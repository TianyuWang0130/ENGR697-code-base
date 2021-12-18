package com.example.foody.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.foody.R
import com.example.foody.data.model.IngredientsSettlementInfo
import com.example.foody.databinding.ActivityMainBinding
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.*
import java.io.IOException
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        setContentView(R.layout.activity_main)

        // get reference to all views
        var et_user_name = findViewById(R.id.username) as EditText
        var et_password = findViewById(R.id.password) as EditText
        //var btn_reset = findViewById(R.id.btn_reset) as Button
        var btn_submit = findViewById(R.id.login) as Button

       /* btn_reset.setOnClickListener {
            // clearing user_name and password edit text views on reset button click
            et_user_name.setText("")
            et_password.setText("")
        }

        */

        // set on-click listener
        btn_submit.setOnClickListener {
            val user_name = et_user_name.text;
            val password = et_password.text;
            Toast.makeText(this@MainActivity, user_name, Toast.LENGTH_LONG).show()

            // your code to validate the user_name and password combination
            // and verify the same

        }
        */
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.AppTheme)
        setContentView(binding.root)

        navController = findNavController(R.id.navHostFragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.recipesFragment,
                R.id.favoriteRecipesFragment,
                R.id.shoppingCartFragment
            )
        )

        binding.bottomNavigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfiguration)


        getKrogerToken()

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getKrogerToken() {
        val client = OkHttpClient()

        val mediaType: MediaType? = MediaType.parse("application/x-www-form-urlencoded")
        val body = RequestBody.create(mediaType, "grant_type=client_credentials&scope=product.compact")
        val encoder = Base64.getEncoder()
        val base64Str =encoder.encodeToString("foodapp-3bba724f4f3d8d431dfc73700f7e9b0485495420779171815:M4ccxZLx52lXh15Oz4vIO1G4EoWaiRzZMEJEb3NU".encodeToByteArray())

//        val base64Str =encoder.encodeToString("foodyapp697-13acadf60a98b8d7d4ee64c1039b53ee3363631535121586690:5xfK9kXsJEvKYrZgZm6HCu19tcEOYfFEnfkZZsRv".encodeToByteArray())
//        val base64Str =
//            encoder.encodeToString("mytestapp100-1a17fc2ddddfafffe9b8fb0ced8da2b64849311008363592103:NLX607HjoxZ70DEa2XpWJb4lrYAwi8x2jTk4VpZy".encodeToByteArray())
//        Log.i("response", "base64Str: $base64Str" )
        val request: Request = Request.Builder()
            .url("https://api.kroger.com/v1/connect/oauth2/token")
            .post(body)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("Authorization", "Basic $base64Str")
            .build()
        //Asynchronous request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                // Get API access_token
                val bodyStr = response.body()?.string()
                Log.i("response", "access_token: $bodyStr" )
                if (response.code() == 200) {
                    response.body()?.let {
                        JsonParser.parseString(bodyStr).asJsonObject?.apply {
                            token = get("access_token").toString()
                            Log.i("response", "access_token: " + get("access_token"))
                        }
                    }
                }
            }
        })
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    companion object {
        var token = ""
    }
}