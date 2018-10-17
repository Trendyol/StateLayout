package com.erkutaras.statelayout

import android.content.Context
import android.support.annotation.LayoutRes
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout

/**
 * Created by erkutaras on 9.09.2018.
 */
class StateLayout @JvmOverloads constructor(context: Context,
                                            attrs: AttributeSet? = null,
                                            defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    private var contentLayout: View? = null
    private var loadingLayout: View? = null
    private var errorLayout: View? = null
    private var loadingWithContentLayout: View? = null

    private var state: State = State.CONTENT
    var onStateLayoutListener: OnStateLayoutListener? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupContentState()
        setupLoadingState()
        setupErrorState()
        setupLoadingWithContentState()

        checkChildCount()
    }

    private fun setupContentState() {
        contentLayout = getChildAt(0)
        contentLayout?.visibility = View.VISIBLE
    }

    private fun setupLoadingState() {
        loadingLayout = inflate(R.layout.layout_state_loading)
        loadingLayout?.visibility = View.GONE
        addView(loadingLayout)
    }

    private fun setupErrorState() {
        errorLayout = inflate(R.layout.layout_state_error)
        errorLayout?.findViewById<Button>(R.id.button_state_layout_error)?.setOnClickListener {
            onStateLayoutListener?.onErrorStateButtonClick()
        }
        errorLayout?.visibility = View.GONE
        addView(errorLayout)
    }

    private fun setupLoadingWithContentState() {
        loadingWithContentLayout = inflate(R.layout.layout_state_loading_with_content)
        loadingWithContentLayout?.visibility = View.GONE
        addView(loadingWithContentLayout)
    }

    fun loading() {
        state = State.LOADING
        loadingLayout?.visibility = View.VISIBLE
        contentLayout?.visibility = View.GONE
        errorLayout?.visibility = View.GONE
        loadingWithContentLayout?.visibility = GONE
    }

    fun content() {
        state = State.CONTENT
        loadingLayout?.visibility = View.GONE
        contentLayout?.visibility = View.VISIBLE
        errorLayout?.visibility = View.GONE
        loadingWithContentLayout?.visibility = GONE
    }

    fun error() {
        state = State.ERROR
        loadingLayout?.visibility = View.GONE
        contentLayout?.visibility = View.GONE
        errorLayout?.visibility = View.VISIBLE
        loadingWithContentLayout?.visibility = GONE
    }

    fun loadingWithContent() {
        state = State.LOADING_WITH_CONTENT
        loadingLayout?.visibility = View.GONE
        contentLayout?.visibility = View.VISIBLE
        errorLayout?.visibility = View.GONE
        loadingWithContentLayout?.visibility = View.VISIBLE
    }

    private fun checkChildCount() {
        if (childCount > 4 || childCount == 0) {
            throwChildCountException()
        }
    }

    private fun throwChildCountException(): Nothing =
            throw IllegalStateException("StateLayout can host only one direct child")

    private fun inflate(@LayoutRes layoutId: Int): View? {
        return LayoutInflater.from(context).inflate(layoutId, null)
    }

}