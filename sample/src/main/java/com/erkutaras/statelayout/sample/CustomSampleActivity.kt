package com.erkutaras.statelayout.sample

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.erkutaras.statelayout.sample.databinding.ActivityCustomSampleBinding

/**
 * Created by erkutaras on 21.12.2018.
 */
private const val WEB_URL = "https://medium.com/@erkutaras"

class CustomSampleActivity : SampleBaseActivity() {

    private lateinit var binding: ActivityCustomSampleBinding
    private var hasError: Boolean = false
    private val contentLoadingProgressBar: ProgressBar
        by lazy { binding.root.findViewById(R.id.contentLoadingProgressBar) }
    private val textviewProgress: TextView by lazy { binding.root.findViewById(R.id.textView_progress) }
    private val buttonRefresh: Button by lazy { binding.root.findViewById(R.id.button_refresh) }
    private val buttonClose: Button by lazy { binding.root.findViewById(R.id.button_close) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomSampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                hasError = false
                if (url.equals(WEB_URL)) binding.stateLayout.loading()
                else binding.stateLayout.loadingWithContent()
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                hasError = true
                showInfoState()
            }
        }
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                contentLoadingProgressBar.progress = newProgress
                textviewProgress.text = "$newProgress%"

                if (!hasError && newProgress == 100) binding.stateLayout.content()
                if (hasError && newProgress == 100) showInfoState()
            }
        }
        loadUrl()
    }

    override fun getMenuResId(): Int = R.menu.menu_custom

    private fun showInfoState() {
        binding.stateLayout.info()
        buttonRefresh.setOnClickListener { loadUrl() }
        buttonClose.setOnClickListener { finish() }
    }

    private fun loadUrl() {
        binding.webView.loadUrl(WEB_URL)
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
