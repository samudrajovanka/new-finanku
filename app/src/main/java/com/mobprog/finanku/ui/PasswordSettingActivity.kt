package com.mobprog.finanku.ui

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.mobprog.finanku.R
import com.mobprog.finanku.data.UpdateUserResponse
import com.mobprog.finanku.network.ApiConfig
import com.mobprog.finanku.preference.AuthPreference
import com.mobprog.finanku.utils.CustomTextWatcher
import com.mobprog.finanku.utils.onChangeRemoveError
import com.mobprog.finanku.view.DialogLoading
import kotlinx.android.synthetic.main.activity_password_setting.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordSettingActivity : AppCompatActivity(), View.OnClickListener {
    private val dialogLoading by lazy { DialogLoading() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_setting)

        initiateUi()
    }

    private val newPasswordTextWatcher = object : CustomTextWatcher() {
        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            super.onTextChanged(text, p1, p2, p3)

            val newPassword = et_new_password_setting.text.toString().trim()
            if (newPassword.length < 8) {
                til_new_password_setting.error = getString(R.string.password_min_error)
            } else {
                til_new_password_setting.error = null
            }
        }
    }

    private fun initiateUi() {
        tv_title_toolbar.text = getString(R.string.change_password_title)

        et_old_password_setting.addTextChangedListener(onChangeRemoveError(til_old_password_setting))
        et_new_password_setting.addTextChangedListener(newPasswordTextWatcher)
        et_confirm_new_password_setting.addTextChangedListener(
            onChangeRemoveError(
                til_confirm_new_password_setting
            )
        )

        ic_back_button_toolbar.setOnClickListener(this)
        btn_change_password.setOnClickListener(this)
    }

    private fun validateForm(): Boolean {
        val oldPassword = et_old_password_setting.text.toString().trim()
        val newPassword = et_new_password_setting.text.toString().trim()
        val confirmNewPassword = et_confirm_new_password_setting.text.toString().trim()
        var isValid = true

        if (TextUtils.isEmpty(oldPassword)) {
            til_old_password_setting.error = "Old password is required"
            isValid = false
        }

        if (TextUtils.isEmpty(newPassword)) {
            til_new_password_setting.error = "New password is required"
            isValid = false
        } else if (newPassword.length < 8) {
            til_new_password_setting.error = getString(R.string.password_min_error)
            isValid = false
        }

        if (TextUtils.isEmpty(confirmNewPassword)) {
            til_confirm_new_password_setting.error = "Confirm new password is required"
            isValid = false
        } else if (confirmNewPassword != newPassword) {
            til_confirm_new_password_setting.error = "Confirm new password didn't match"
            isValid = false
        }

        return isValid
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ic_back_button_toolbar -> {
                onBackPressed()
            }
            R.id.btn_change_password -> {
                if (validateForm()) {
                    til_old_password_setting.error = null
                    til_new_password_setting.error = null
                    til_confirm_new_password_setting.error = null

                    et_old_password_setting.clearFocus()
                    et_new_password_setting.clearFocus()
                    et_confirm_new_password_setting.clearFocus()

                    changePassword(et_new_password_setting.toString())
                }
            }
        }
    }

    private fun changePassword(newPassword: String) {
        dialogLoading.show(supportFragmentManager, "loading")
        val authPreference = AuthPreference(this)

        val client = ApiConfig.getApiService().updatePassword(
            authPreference.getToken(),
            authPreference.getUser().id.toString(),
            password = newPassword
        )

        client.enqueue(object : Callback<UpdateUserResponse> {
            override fun onResponse(
                call: Call<UpdateUserResponse>,
                response: Response<UpdateUserResponse>
            ) {
                dialogLoading.stop()

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val intent = Intent(
                            this@PasswordSettingActivity,
                            AccountSettingActivity::class.java
                        )
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()?.string())
                        Toast.makeText(
                            this@PasswordSettingActivity,
                            jObjError.getJSONObject("error").getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@PasswordSettingActivity, e.message, Toast.LENGTH_LONG)
                            .show()
                    }
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UpdateUserResponse>, t: Throwable) {
                dialogLoading.stop()

                Toast.makeText(
                    this@PasswordSettingActivity,
                    "Error. Please contact developer",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }
}