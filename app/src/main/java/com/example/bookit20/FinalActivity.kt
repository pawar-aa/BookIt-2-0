package com.example.bookit20

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView

class FinalActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "SetTextI18n", "QueryPermissionsNeeded",
        "SetJavaScriptEnabled"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final)

        val oLatitude = intent.getDoubleExtra("Ori_LATITUDE", 0.0)
        val oLongitude = intent.getDoubleExtra("Ori_LONGITUDE", 0.0)
        val dLatitude = intent.getDoubleExtra("Des_LATITUDE", 0.0)
        val dLongitude = intent.getDoubleExtra("Des_LONGITUDE", 0.0)

        val mWebView1 = findViewById<WebView>(R.id.webView1)
        val webSettings1: WebSettings = mWebView1.settings
        webSettings1.javaScriptEnabled = true
        mWebView1.webViewClient = WebViewClient()
        mWebView1.webChromeClient = WebChromeClient()
        val uberUrl = "https://m.uber.com/go/product-selection?" +
                "pickup={\"latitude\":$oLatitude,\"longitude\":$oLongitude}" +
                "&drop={\"latitude\":$dLatitude,\"longitude\":$dLongitude}" +
                "&vehicle=116"
        mWebView1.loadUrl(uberUrl)

        val mWebView2 = findViewById<WebView>(R.id.webView2)
        val webSettings2: WebSettings = mWebView2.settings
        webSettings2.javaScriptEnabled = true
        mWebView2.webViewClient = WebViewClient()
        mWebView2.webChromeClient = WebChromeClient()
        val olaUrl = "https://book.olacabs.com/?" +
                "serviceType=p2p&" +
                "utm_source=widget_on_olacabs&" +
                "lat=$oLatitude&" +
                "lng=$oLongitude&" +
                "pickup=&" +
                "drop_lat=$dLatitude&" +
                "drop_lng=$dLongitude"
        mWebView2.loadUrl(olaUrl)

    }
}