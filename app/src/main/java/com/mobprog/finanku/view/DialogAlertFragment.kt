package com.mobprog.finanku.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mobprog.finanku.R
import com.mobprog.finanku.listener.DialogAlertListener
import kotlinx.android.synthetic.main.dialog_alert.*

class DialogAlertFragment(private val listener: DialogAlertListener) : DialogFragment() {

    private var message = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_alert, container)
    }

    fun setMessage(text: String) {
        message = text
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_dialog_alert_message.text = message

        btn_dialog_alert_action.setOnClickListener {
            listener.action()
            dismiss()
        }
    }
}