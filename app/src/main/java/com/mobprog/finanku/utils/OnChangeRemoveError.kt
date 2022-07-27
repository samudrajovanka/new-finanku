package com.mobprog.finanku.utils

import com.google.android.material.textfield.TextInputLayout

fun onChangeRemoveError(til: TextInputLayout): CustomTextWatcher {
    return object : CustomTextWatcher() {
        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            super.onTextChanged(text, p1, p2, p3)

            til.error = null
        }
    }
}