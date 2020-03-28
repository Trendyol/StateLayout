package com.erkutaras.statelayout.sample

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.erkutaras.statelayout.StateLayout
import kotlinx.android.synthetic.main.activity_custom_sample.stateLayout
import kotlinx.android.synthetic.main.activity_custom_sample.webView
import kotlinx.android.synthetic.main.layout_custom_info.button_close
import kotlinx.android.synthetic.main.layout_custom_info.button_refresh
import kotlinx.android.synthetic.main.layout_custom_loading.contentLoadingProgressBar
import kotlinx.android.synthetic.main.layout_custom_loading.textView_progress

/**
 * Created by erkutaras on 21.12.2018.
 */
private const val WEB_URL = "https://medium.com/@erkutaras"

class CustomSampleActivity : SampleBaseActivity() {

    private var hasError: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_sample)

        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                hasError = false
                if (url.equals(WEB_URL)) stateLayout.showState(StateLayout.provideLoadingStateInfo())
                else stateLayout.showState(StateLayout.provideLoadingWithContentStateInfo())
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                hasError = true
                showInfoState()
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                contentLoadingProgressBar.progress = newProgress
                textView_progress.text = "$newProgress%"

                if (!hasError && newProgress == 100) stateLayout.showState(StateLayout.provideContentStateInfo())
                if (hasError && newProgress == 100) showInfoState()
            }
        }
        loadUrl()
    }

    override fun getMenuResId(): Int = R.menu.menu_custom

    private fun showInfoState() {
        stateLayout.showState(StateLayout.provideInfoStateInfo())
        button_refresh.setOnClickListener { loadUrl() }
        button_close.setOnClickListener { finish() }
    }

    private fun loadUrl() {
        webView.loadUrl(WEB_URL)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }
}