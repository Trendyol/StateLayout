package com.erkutaras.statelayout.sample

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.erkutaras.statelayout.StateLayout
import kotlinx.android.synthetic.main.activity_state_layout_sample.stateLayout
import kotlinx.android.synthetic.main.activity_state_layout_sample.webView

/**
 * Created by erkutaras on 2.02.2019.
 */
private const val WEB_URL = "https://github.com/erkutaras"

class AnimationLoadingSampleActivity : SampleBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation_loading_sample)

        webView.webViewClient = SampleWebViewClient(stateLayout) { onStateLayoutInfoButtonClick() }
        webView.loadUrl(WEB_URL)
    }

    override fun getMenuResId(): Int = R.menu.menu_animation_loading

    private fun onStateLayoutInfoButtonClick() {
        webView.loadUrl(WEB_URL)
        Toast.makeText(this, "Refreshing Page...", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }

    private class SampleWebViewClient(val stateLayout: StateLayout, val listener: () -> Unit) :
        WebViewClient() {

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

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            hasError = true
            stateLayout.infoImage(R.drawable.ic_android_black_64dp)
                .infoTitle("Ooops.... :(")
                .infoMessage("Unexpected error occurred. Please refresh the page!")
                .infoButtonText("Refresh")
                .infoButtonListener(listener)
        }

    }
}