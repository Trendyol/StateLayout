package com.erkutaras.statelayout.sample

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.erkutaras.statelayout.State
import com.erkutaras.statelayout.StateInfo
import com.erkutaras.statelayout.StateLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_error_sample.buttonShowContent
import kotlinx.android.synthetic.main.activity_error_sample.buttonShowError
import kotlinx.android.synthetic.main.activity_error_sample.stateLayout

/**
 * Created by erkutaras on 21.12.2018.
 */

class ErrorSampleActivity : SampleBaseActivity() {

    override fun getMenuResId(): Int = R.menu.menu_error

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error_sample)
        stateLayout.decider = object : StateLayout.Decider {
            override fun decide(stateLayout: StateLayout, stateInfo: StateInfo) {
                if (stateInfo.state == State.ERROR) {
                    showBottomSheetErrorDialog(stateInfo)
                } else {
                    super.decide(stateLayout, stateInfo)
                }
            }
        }
        buttonShowContent.setOnClickListener {
            showContentState()
        }
        buttonShowError.setOnClickListener {
            showErrorState()
        }
    }

    private fun showBottomSheetErrorDialog(stateInfo: StateInfo) {
        val bottomSheetDialog = ErrorBottomSheetDialog(this@ErrorSampleActivity, stateInfo)
        bottomSheetDialog.show()
    }

    private fun showContentState() {
        stateLayout.showState(StateLayout.provideContentStateInfo())
    }

    private fun showErrorState() {
        val errorState = StateInfo(
            state = State.ERROR,
            infoImage = android.R.drawable.stat_notify_error,
            infoMessage = "Error occurred",
            infoButtonText = "Try Again",
            onInfoButtonClick = {
                showContentState()
            })
        stateLayout.showState(errorState)
    }

    inner class ErrorBottomSheetDialog(context: Context, stateInfo: StateInfo) :
        BottomSheetDialog(context) {

        init {
            val inflatedView =
                LayoutInflater.from(context)
                    .inflate(R.layout.dialog_bottom_sheet_error, null, false)
            setContentView(inflatedView)
            findViewById<ImageView>(R.id.imageViewStateLayoutInfo)
                ?.setImageResource(stateInfo.infoImage ?: 0)
            findViewById<TextView>(R.id.textViewStateLayoutInfoMessage)?.text =
                stateInfo.infoMessage
            findViewById<Button>(R.id.buttonStateLayoutInfo)?.text =
                stateInfo.infoButtonText
            findViewById<Button>(R.id.buttonStateLayoutInfo)
                ?.setOnClickListener {
                    dismiss()
                    stateInfo.onInfoButtonClick?.invoke()
                }
            val bottomSheet = findViewById<View>(R.id.design_bottom_sheet);
            bottomSheet?.layoutParams?.height =
                resources.getDimensionPixelSize(R.dimen.bottom_sheet_dialog_height)
        }
    }
}