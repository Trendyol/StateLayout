package com.erkutaras.statelayout

import android.view.animation.Animation

data class StateInfo(
    val infoImage: Int? = null,
    val infoTitle: String? = null,
    val infoMessage: String? = null,
    val infoButtonText: String? = null,
    val state: State = State.INFO,
    val onInfoButtonClick: (() -> Unit)? = null,
    val loadingAnimation: Animation? = null,
    val loadingWithContentAnimation: Animation? = null,
    val loadingMessage: String? = null
)