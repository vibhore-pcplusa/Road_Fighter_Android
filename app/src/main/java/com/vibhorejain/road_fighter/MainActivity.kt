package com.vibhorejain.road_fighter

import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.addCallback

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true

            webViewClient = object : WebViewClient() {
                override fun onReceivedError(
                    view: WebView,
                    request: WebResourceRequest,
                    error: WebResourceError
                ) {
                    if (request.isForMainFrame) {
                        showOfflinePage()
                    }
                }

                override fun onReceivedHttpError(
                    view: WebView,
                    request: WebResourceRequest,
                    errorResponse: WebResourceResponse
                ) {
                    if (request.isForMainFrame) {
                        showOfflinePage()
                    }
                }
            }

            loadUrl("https://www.legalchalo.com/vj/road_fighter/")
        }

        setContentView(webView)

        onBackPressedDispatcher.addCallback(this) {
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                finish()
            }
        }
    }

    private fun showOfflinePage() {
        val html = """
            <html>
              <head>
                <meta charset="utf-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <style>
                  body {
                    margin: 0;
                    font-family: Arial, sans-serif;
                    background: #0b0b0b;
                    color: white;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    min-height: 100vh;
                  }
                  .box {
                    width: 90%;
                    max-width: 420px;
                    text-align: center;
                    padding: 24px;
                    border: 2px solid #1db954;
                    border-radius: 18px;
                    background: #161616;
                    box-shadow: 0 8px 24px rgba(0,0,0,0.35);
                  }
                  h1 {
                    margin: 0 0 12px;
                    font-size: 24px;
                  }
                  p {
                    margin: 0 0 18px;
                    line-height: 1.5;
                    color: #d9d9d9;
                  }
                  button {
                    background: #1db954;
                    color: black;
                    border: none;
                    padding: 12px 18px;
                    border-radius: 10px;
                    font-size: 16px;
                    font-weight: 600;
                  }
                </style>
              </head>
              <body>
                <div class="box">
                  <h1>You are offline</h1>
                  <p>Please turn on Wi-Fi or mobile data and tap Refresh to continue.</p>
                  <button onclick="window.location.reload()">Refresh</button>
                </div>
              </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
    }
}