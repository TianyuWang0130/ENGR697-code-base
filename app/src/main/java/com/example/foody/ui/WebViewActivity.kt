package com.example.foody.ui

import android.os.Build
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.foody.R
import com.example.foody.databinding.ActivityWebviewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.HashMap

@AndroidEntryPoint
class WebViewActivity :AppCompatActivity(){
    lateinit var binding:ActivityWebviewBinding
    val headerMap= HashMap<String,String>()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setTheme(R.style.AppTheme)
        setContentView(binding.root)
        binding.webView.webViewClient=object :WebViewClient(){

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(request?.url.toString())
                return true
//                return super.shouldOverrideUrlLoading(view, request)
            }
        }

        loadUrl()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadUrl(){
        val base64Str =
                   Base64.getEncoder().encodeToString("mytestapp100-1a17fc2ddddfafffe9b8fb0ced8da2b64849311008363592103:NLX607HjoxZ70DEa2XpWJb4lrYAwi8x2jTk4VpZy".encodeToByteArray())

        //val base64Str =Base64.getEncoder().encodeToString("foodapp-3bba724f4f3d8d431dfc73700f7e9b0485495420779171815:M4ccxZLx52lXh15Oz4vIO1G4EoWaiRzZMEJEb3NU".encodeToByteArray())
        headerMap["Authorization"] = "Basic $base64Str"
        headerMap["Content-Type"] = "application/x-www-form-urlencoded"
        binding.webView.loadUrl("https://api.kroger.com/v1/connect/oauth2/authorize?scope=profile.compact&client_id=mytestapp100-1a17fc2ddddfafffe9b8fb0ced8da2b64849311008363592103&redirect_uri=http://localhost:3000/callback&response_type=code",headerMap)
    }
}