package com.vibhorejain.road_fighter

import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.view.ViewGroup
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import android.content.Intent
import android.content.Context
import android.webkit.JavascriptInterface
import android.media.AudioManager
import android.widget.Button
import android.view.Gravity
import android.widget.FrameLayout
import androidx.annotation.Keep

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView
    private var isMuted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Mobile Ads SDK
        MobileAds.initialize(this) {}

        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        webView = WebView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            addJavascriptInterface(WebAppInterface(this@MainActivity), "AndroidApp")

            webViewClient = object : WebViewClient() {
                override fun onReceivedError(
                    view: WebView,
                    request: WebResourceRequest,
                    error: WebResourceError
                ) {
                    Log.e("WebViewError", "Error: ${error.errorCode}, Description: ${error.description}, URL: ${request.url}")
                    if (request.isForMainFrame) {
                        showOfflinePage()
                    }
                }

                override fun onReceivedHttpError(
                    view: WebView,
                    request: WebResourceRequest,
                    errorResponse: WebResourceResponse
                ) {
                    Log.e("WebViewHttpError", "Status Code: ${errorResponse.statusCode}, URL: ${request.url}")
                    if (request.isForMainFrame) {
                        showOfflinePage()
                    }
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url?.toString() ?: return false
                    
                    if (url.startsWith("intent://") || url.startsWith("market://")) {
                        try {
                            val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                            if (intent != null) {
                                startActivity(intent)
                                return true
                            }
                        } catch (e: Exception) {
                            Log.e("WebView", "Error handling intent URL: ${e.message}")
                            return true
                        }
                    }
                    return false
                }
            }

            //loadUrl("https://www.legalchalo.com/vj/road_fighter/")
            val gameUrl = if (BuildConfig.DEBUG) {
                // If emulator, use 10.0.2.2. If real device, you MUST use your computer's IP address.
                // To start server in localhost:8000  terminal use command-  php -S 0.0.0.0:8000
                "http://10.0.2.2:8000/"
            } else {
                "https://www.legalchalo.com/vj/road_fighter/"
            }
            Log.d("GameURL", "Loading: $gameUrl")
            loadUrl(gameUrl)
        }

        val adView = AdView(this).apply {
            adUnitId = if (BuildConfig.DEBUG) {
                "ca-app-pub-3940256099942544/6300978111" // Test ID
            } else {
                //"ca-app-pub-8728236576053953/5027228832" // Production ID
                "ca-app-pub-3940256099942544/6300978111"
            }
            
            // Adaptive ad size for 100% width
            val displayMetrics = resources.displayMetrics
            val adWidth = (displayMetrics.widthPixels / displayMetrics.density).toInt()
            setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this@MainActivity, adWidth))

            val adLayoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM
            }
            layoutParams = adLayoutParams
            loadAd(AdRequest.Builder().build())
        }

        val muteButton = Button(this).apply {
            text = "🔊"
            // Increase icon size
            textSize = 24f
            // White rounded corner background
            val shape = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                cornerRadius = resources.displayMetrics.density * 10
                setColor(android.graphics.Color.WHITE)
            }
            background = shape
            
            val size = (resources.displayMetrics.density * 50).toInt()
            // Place button above the ad. Adaptive ad height is roughly 60dp.
            val adHeight = (resources.displayMetrics.density * 60).toInt()
            
            layoutParams = FrameLayout.LayoutParams(size, size).apply {
                gravity = Gravity.BOTTOM or Gravity.START
                setMargins(0, 0, 0, adHeight)
            }
            setPadding(0, 0, 0, 0)
            
            setOnClickListener {
                isMuted = !isMuted
                if (isMuted) {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
                    text = "🔇"
                } else {
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0)
                    text = "🔊"
                }
            }
        }

        val rootLayout = FrameLayout(this).apply {
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            addView(webView)
            addView(adView)
            addView(muteButton)
        }

        setContentView(rootLayout)

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
                  <p>Please turn on Wi-Fi or mobile data and then close the app and reopen it.</p>
                  
                </div>
              </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
    }
}

@Keep
class WebAppInterface(private val mContext: Context) {
    @JavascriptInterface
    fun shareUrl(title: String, text: String, url: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, title)
            putExtra(Intent.EXTRA_TEXT, "$text\n$url")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share game via...")
        mContext.startActivity(shareIntent)
    }
}