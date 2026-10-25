package com.resence.weresence

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

/**
 * Camouflage Decoy Activity (ai.resence.in Integration).
 *
 * Triggered under duress when the user enters Decoy PIN `0000`:
 * - All WeResence messaging, private vaults, phonebooks, and offline SMS are completely silenced.
 * - Displays the authentic, live 'Resence AI' educational & intelligence assistant (ai.resence.in).
 * - Anyone physically inspecting the device sees only a harmless, everyday AI productivity tool.
 */
class DecoyAiActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)

        // Enforce anti-screenshot protection
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        webView = WebView(this).apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                cacheMode = WebSettings.LOAD_DEFAULT
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            }
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            loadUrl("https://ai.resence.in")
        }

        setContentView(webView)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
