package com.mobprog.finanku.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mobprog.finanku.R
import com.mobprog.finanku.listener.DialogItemListener
import kotlinx.android.synthetic.main.dialog_prompt.*

class DialogPromptFragment(private val listener: DialogItemListener) : DialogFragment() {

    private var message = ""
    private var textPositiveBtn = "Yes"
    private var textNegativeBtn = "Cancel"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_prompt, container)
    }

    fun setMessage(text: String) {
        message = text
    }

    fun setTextPositiveBtn(text: String) {
        textPositiveBtn = text
    }

    fun setTextNegativeBtn(text: String) {
        textNegativeBtn = text
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_dialog_prompt_message.text = message
        btn_dialog_prompt_positive_action.text = textPositiveBtn
        btn_dialog_prompt_negative_action.text = textNegativeBtn

        btn_dialog_prompt_negative_action.setOnClickListener {
            listener.getUserChoice(false)
            dismiss()
        }

        btn_dialog_prompt_positive_action.setOnClickListener {
            listener.getUserChoice(true)
            dismiss()
        }
    }
}