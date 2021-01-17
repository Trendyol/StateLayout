package com.erkutaras.statelayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewStub
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by erkutaras on 9.09.2018.
 */
class StateLayout @JvmOverloads constructor(context: Context,
                                            attrs: AttributeSet? = null,
                                            defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private var contentLayout: View? = null
    private var loadingLayout: ViewStub? = null
    private var infoLayout: ViewStub? = null
    private var loadingWithContentLayout: ViewStub? = null

    private var state: State = State.CONTENT

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupContentState()
        setupLoadingState()
        setupInfoState()
        setupLoadingWithContentState()

        checkChildCount()
    }

    private fun setupContentState() {
        contentLayout = getChildAt(0)
        contentLayout?.visibility = View.VISIBLE
    }

    private fun setupLoadingState() {
        loadingLayout = ViewStub(context, R.layout.layout_state_loading)
        addView(loadingLayout)
    }

    private fun setupInfoState() {
        infoLayout = ViewStub(context, R.layout.layout_state_info)
        addView(infoLayout)
    }

    private fun setupLoadingWithContentState() {
        loadingWithContentLayout = ViewStub(context, R.layout.layout_state_loading_with_content)
        addView(loadingWithContentLayout)
    }

    fun loading(): StateLayout {
        state = State.LOADING
        loadingLayout?.visibility = View.VISIBLE
        contentLayout?.visibility = View.GONE
        infoLayout?.visibility = View.GONE
        loadingWithContentLayout?.visibility = GONE
        return this
    }

    fun content(): StateLayout {
        state = State.CONTENT
        loadingLayout?.visibility = View.GONE
        contentLayout?.visibility = View.VISIBLE
        infoLayout?.visibility = View.GONE
        loadingWithContentLayout?.visibility = GONE
        return this
    }

    fun info(): StateLayout {
        state = State.INFO
        loadingLayout?.visibility = View.GONE
        contentLayout?.visibility = View.GONE
        infoLayout?.visibility = View.VISIBLE
        loadingWithContentLayout?.visibility = GONE
        return this
    }

    fun loadingWithContent(): StateLayout {
        state = State.LOADING_WITH_CONTENT
        loadingLayout?.visibility = View.GONE
        contentLayout?.visibility = View.VISIBLE
        infoLayout?.visibility = View.GONE
        loadingWithContentLayout?.visibility = View.VISIBLE
        return this
    }

    private fun checkChildCount() {
        if (childCount > 4 || childCount == 0) {
            throwChildCountException()
        }
    }

    private fun throwChildCountException(): Nothing = throw IllegalStateException("StateLayout can host only one direct child")

    inner class InfoLayoutBuilder {
        init {
            info()
        }

        fun infoImage(imageRes: Int): InfoLayoutBuilder {
            findViewById<ImageView>(R.id.imageView_state_layout_info)?.let {
                it.setImageResource(imageRes)
                it.visibility = View.VISIBLE
            }
            return this
        }

        fun infoTitle(title: String): InfoLayoutBuilder {
            infoLayout?.findViewById<TextView>(R.id.textView_state_layout_info_title)?.let {
                it.text = title
                it.visibility = View.VISIBLE
            }
            return this
        }

        fun infoMessage(message: String): InfoLayoutBuilder {
            findViewById<TextView>(R.id.textView_state_layout_info_message)?.let {
                it.text = message
                it.visibility = View.VISIBLE
            }
            return this
        }

        fun infoButtonListener(onStateLayoutListener: OnStateLayoutListener?): InfoLayoutBuilder {
            findViewById<Button>(R.id.button_state_layout_info)?.setOnClickListener {
                onStateLayoutListener?.onStateLayoutInfoButtonClick()
            }
            return this
        }

        fun infoButtonText(buttonText: String): InfoLayoutBuilder {
            findViewById<Button>(R.id.button_state_layout_info)?.let {
                it.text = buttonText
                it.visibility = View.VISIBLE
            }
            return this
        }

        fun infoButton(buttonText: String, onStateLayoutListener: OnStateLayoutListener?): InfoLayoutBuilder {
            findViewById<Button>(R.id.button_state_layout_info)?.let { it ->
                it.text = buttonText
                it.setOnClickListener { onStateLayoutListener?.onStateLayoutInfoButtonClick() }
                it.visibility = View.VISIBLE
            }
            return this
        }
    }

    interface OnStateLayoutListener {
        fun onStateLayoutInfoButtonClick()
    }

    enum class State {
        LOADING, CONTENT, INFO, LOADING_WITH_CONTENT
    }
}