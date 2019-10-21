package com.erkutaras.statelayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.erkutaras.statelayout.StateLayout.State.CONTENT
import com.erkutaras.statelayout.StateLayout.State.EMPTY
import com.erkutaras.statelayout.StateLayout.State.ERROR
import com.erkutaras.statelayout.StateLayout.State.INFO
import com.erkutaras.statelayout.StateLayout.State.LOADING
import com.erkutaras.statelayout.StateLayout.State.LOADING_WITH_CONTENT
import com.erkutaras.statelayout.StateLayout.State.NONE

/**
 * Created by erkutaras on 9.09.2018.
 */
class StateLayout : FrameLayout {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        if (isInEditMode) {
            state = CONTENT
        }

        context.theme.obtainStyledAttributes(attrs, R.styleable.StateLayout, 0, 0)
            .apply {
                try {
                    state = State.values()[getInteger(R.styleable.StateLayout_state, NONE.ordinal)]
                    loadingLayoutRes = getResourceId(R.styleable.StateLayout_loadingLayout, R.layout.layout_state_loading)
                    infoLayoutRes = getResourceId(R.styleable.StateLayout_infoLayout, R.layout.layout_state_info)
                    loadingWithContentLayoutRes = getResourceId(R.styleable.StateLayout_loadingWithContentLayout, R.layout.layout_state_loading_with_content)

                    getResourceId(R.styleable.StateLayout_loadingAnimation, 0).notZero {
                        loadingAnimation = AnimationUtils.loadAnimation(context, it)
                    }
                    getResourceId(R.styleable.StateLayout_loadingWithContentAnimation, 0).notZero {
                        loadingWithContentAnimation = AnimationUtils.loadAnimation(context, it)
                    }
                } finally {
                    recycle()
                }
            }
    }

    private var contentLayout: View? = null
    private var loadingLayout: View? = null
    private var infoLayout: View? = null
    private var loadingWithContentLayout: View? = null

    private var state: State = NONE

    @LayoutRes
    private var loadingLayoutRes: Int = R.layout.layout_state_loading
    @LayoutRes
    private var infoLayoutRes: Int = R.layout.layout_state_info
    @LayoutRes
    private var loadingWithContentLayoutRes: Int = R.layout.layout_state_loading_with_content

    private var loadingAnimation: Animation? = null
    private var loadingWithContentAnimation: Animation? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupContentState()
        setupLoadingState()
        setupInfoState()
        setupLoadingWithContentState()

        updateWithState()
        checkChildCount()
    }

    private fun setupContentState() {
        contentLayout = getChildAt(0)
        contentLayout?.gone()
    }

    private fun setupLoadingState() {
        loadingLayout = inflate(loadingLayoutRes)
        loadingLayout?.gone()
        addView(loadingLayout)
    }

    private fun setupInfoState() {
        infoLayout = inflate(infoLayoutRes)
        infoLayout?.gone()
        addView(infoLayout)
    }

    private fun setupLoadingWithContentState() {
        loadingWithContentLayout = inflate(loadingWithContentLayoutRes)
        loadingWithContentLayout?.gone()
        addView(loadingWithContentLayout)
    }

    private fun updateWithState() {
        when (state) {
            LOADING -> loading()
            CONTENT -> content()
            INFO, ERROR, EMPTY -> info()
            LOADING_WITH_CONTENT -> loadingWithContent()
            NONE -> hideAll()
        }
    }

    private fun checkChildCount() {
        if (childCount > 4 || childCount == 0) {
            throwChildCountException()
        }
    }

    private fun hideAll() {
        updateLoadingVisibility(GONE)
        contentLayout.gone()
        infoLayout.gone()
        updateLoadingWithContentVisibility(GONE)
    }

    private fun updateLoadingVisibility(visibility: Int) =
        when (visibility) {
            VISIBLE -> loadingLayout.visible {
                it.startViewAnimation(
                    R.id.customView_state_layout_loading,
                    loadingAnimation
                )
            }
            else -> loadingLayout.gone { it.clearViewAnimation(R.id.customView_state_layout_loading) }
        }

    private fun updateLoadingWithContentVisibility(visibility: Int) =
        when (visibility) {
            VISIBLE -> loadingWithContentLayout.visible {
                it.startViewAnimation(
                    R.id.customView_state_layout_with_content,
                    loadingWithContentAnimation
                )
            }
            else -> loadingWithContentLayout.gone { it.clearViewAnimation(R.id.customView_state_layout_with_content) }
        }

    private fun throwChildCountException(): Nothing =
        throw IllegalStateException("StateLayout can host only one direct child")

    fun initialState(state: State) {
        this.state = state
    }

    fun loadingMessage(message: String): StateLayout {
        loadingLayout.findView<TextView>(R.id.textView_state_layout_loading_message) {
            text = message
            visible()
        }
        return loading()
    }

    fun loadingAnimation(animation: Animation): StateLayout {
        loadingAnimation = animation
        return loading()
    }

    fun loading(): StateLayout {
        state = LOADING
        updateLoadingVisibility(VISIBLE)
        contentLayout.gone()
        infoLayout.gone()
        updateLoadingWithContentVisibility(GONE)
        return this
    }

    fun loading(@LayoutRes layoutId: Int) {
        this.loadingLayoutRes = layoutId
        removeView(loadingLayout)
        setupLoadingState()
        showState(provideLoadingStateInfo())
    }

    fun content(): StateLayout {
        state = CONTENT
        updateLoadingVisibility(GONE)
        contentLayout.visible()
        infoLayout.gone()
        updateLoadingWithContentVisibility(GONE)
        return this
    }

    fun infoImage(imageRes: Int): StateLayout {
        infoLayout.findView<ImageView>(R.id.imageView_state_layout_info) {
            setImageResource(imageRes)
            visible()
        }
        return info()
    }

    fun infoTitle(title: String): StateLayout {
        infoLayout.findView<TextView>(R.id.textView_state_layout_info_title) {
            text = title
            visible()
        }
        return info()
    }

    fun infoMessage(message: String): StateLayout {
        infoLayout.findView<TextView>(R.id.textView_state_layout_info_message) {
            text = message
            visible()
        }
        return info()
    }

    fun infoButtonListener(block: () -> Unit) {
        infoLayout.findView<Button>(R.id.button_state_layout_info) {
            setOnClickListener { block.invoke() }
        }
        info()
    }

    fun infoButtonText(buttonText: String): StateLayout {
        infoLayout.findView<Button>(R.id.button_state_layout_info) {
            text = buttonText
            visible()
        }
        return info()
    }

    fun info(): StateLayout {
        state = INFO
        updateLoadingVisibility(GONE)
        contentLayout.gone()
        infoLayout.visible()
        updateLoadingWithContentVisibility(GONE)
        return this
    }

    fun info(@LayoutRes layoutId: Int) {
        this.infoLayoutRes = layoutId
        removeView(infoLayout)
        setupInfoState()
        showState(provideInfoStateInfo())
    }

    fun loadingWithContentAnimation(animation: Animation): StateLayout {
        loadingWithContentAnimation = animation
        return loadingWithContent()
    }

    fun loadingWithContent(): StateLayout {
        state = LOADING_WITH_CONTENT
        updateLoadingVisibility(GONE)
        contentLayout.visible()
        infoLayout.gone()
        updateLoadingWithContentVisibility(VISIBLE)
        return this
    }

    fun loadingWithContent(@LayoutRes layoutId: Int) {
        this.loadingWithContentLayoutRes = layoutId
        removeView(loadingWithContentLayout)
        setupLoadingWithContentState()
        showState(provideLoadingWithContentStateInfo())
    }

    fun showLoading(stateInfo: StateInfo?) = showState(stateInfo)

    fun showContent(stateInfo: StateInfo?) = showState(stateInfo)

    fun showInfo(stateInfo: StateInfo?) = showState(stateInfo)

    fun showLoadingWithContent(stateInfo: StateInfo?) = showState(stateInfo)

    fun showError(stateInfo: StateInfo?) = showState(stateInfo)

    fun showEmpty(stateInfo: StateInfo?) = showState(stateInfo)

    fun showState(stateInfo: StateInfo?) {
        loadingAnimation = stateInfo?.loadingAnimation
        loadingWithContentAnimation = stateInfo?.loadingWithContentAnimation
        when (stateInfo?.state) {
            LOADING -> loading()
            CONTENT -> content()
            LOADING_WITH_CONTENT -> loadingWithContent()
            INFO, ERROR, EMPTY -> {
                stateInfo.infoImage?.let { infoImage(it) }
                stateInfo.infoTitle?.let { infoTitle(it) }
                stateInfo.infoMessage?.let { infoMessage(it) }
                stateInfo.infoButtonText?.let { infoButtonText(it) }
                stateInfo.onInfoButtonClick?.let { infoButtonListener(it) }
            }
            null, NONE -> hideAll()
        }
    }

    companion object {
        @JvmStatic
        fun provideLoadingStateInfo() = StateInfo(state = LOADING)

        @JvmStatic
        fun provideContentStateInfo() = StateInfo(state = CONTENT)

        @JvmStatic
        fun provideErrorStateInfo() = StateInfo(state = ERROR)

        @JvmStatic
        fun provideLoadingWithContentStateInfo() = StateInfo(state = LOADING_WITH_CONTENT)

        @JvmStatic
        fun provideInfoStateInfo() = StateInfo(state = INFO)

        @JvmStatic
        fun provideEmptyStateInfo() = StateInfo(state = EMPTY)

        @JvmStatic
        fun provideNoneStateInfo() = StateInfo(state = NONE)
    }

    enum class State {
        LOADING, CONTENT, INFO, LOADING_WITH_CONTENT, ERROR, EMPTY, NONE
    }

    data class StateInfo(
        val infoImage: Int? = null,
        val infoTitle: String? = null,
        val infoMessage: String? = null,
        val infoButtonText: String? = null,
        val state: StateLayout.State = INFO,
        val onInfoButtonClick: (() -> Unit)? = null,
        val loadingAnimation: Animation? = null,
        val loadingWithContentAnimation: Animation? = null
    )
}
