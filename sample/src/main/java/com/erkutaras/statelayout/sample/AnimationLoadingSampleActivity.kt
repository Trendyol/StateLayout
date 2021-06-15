package com.erkutaras.statelayout.sample

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.erkutaras.statelayout.StateLayout
import com.erkutaras.statelayout.sample.databinding.ActivityAnimationLoadingSampleBinding

/**
 * Created by erkutaras on 2.02.2019.
 */
private const val WEB_URL = "https://github.com/erkutaras"

class AnimationLoadingSampleActivity : SampleBaseActivity(), StateLayout.OnStateLayoutListener {

    private lateinit var binding: ActivityAnimationLoadingSampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimationLoadingSampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.webViewClient = SampleWebViewClient(binding.stateLayout, this)
        binding.webView.loadUrl(WEB_URL)
    }

    override fun getMenuResId(): Int = R.menu.menu_animation_loading

    override fun onStateLayoutInfoButtonClick() {
        binding.webView.loadUrl(WEB_URL)
        Toast.makeText(this, "Refreshing Page...", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private class SampleWebViewClient(
        val stateLayout: StateLayout,
        val onStateLayoutListener: StateLayout.OnStateLayoutListener
    ) : WebViewClient() {

        var hasError: Boolean = false

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            hasError = false
            if (url.equals(WEB_URL)) stateLayout.loading()
            else stateLayout.loadingWithContent()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            if (hasError.not()) stateLayout.content()
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            hasError = true
            stateLayout.infoImage(R.drawable.ic_android_black_64dp)
                .infoTitle("Ooops.... :(")
                .infoMessage("Unexpected error occurred. Please refresh the page!")
                .infoButtonText("Refresh")
                .infoButtonListener {
                    onStateLayoutListener.onStateLayoutInfoButtonClick()
                }
        }
    }
}
