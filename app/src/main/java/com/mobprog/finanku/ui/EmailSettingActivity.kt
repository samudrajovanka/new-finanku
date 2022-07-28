package com.mobprog.finanku.ui

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.mobprog.finanku.R
import com.mobprog.finanku.data.UpdateUserResponse
import com.mobprog.finanku.network.ApiConfig
import com.mobprog.finanku.preference.AuthPreference
import com.mobprog.finanku.utils.CustomTextWatcher
import com.mobprog.finanku.view.DialogLoading
import kotlinx.android.synthetic.main.activity_email.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmailSettingActivity : AppCompatActivity(), View.OnClickListener {
    private val dialogLoading by lazy { DialogLoading() }

    private lateinit var authPreference: AuthPreference
    private lateinit var oldEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        initiateUi()
    }

    private val emailTextWatcher = object : CustomTextWatcher() {
        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            super.onTextChanged(text, p1, p2, p3)

            val email = et_email_setting.text.toString().trim()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                til_email_setting.error = "Invalid email"
            } else if (email == oldEmail) {
                btn_change_email.isEnabled = false
            } else {
                til_email_setting.error = null
                btn_change_email.isEnabled = true
            }
        }
    }

    private fun initiateUi() {
        authPreference = AuthPreference(this)
        oldEmail = authPreference.getUser().email.toString()

        tv_title_toolbar.text = getString(R.string.email_title)
        et_email_setting.setText(oldEmail)
        btn_change_email.isEnabled = false

        et_email_setting.addTextChangedListener(emailTextWatcher)

        ic_back_button_toolbar.setOnClickListener(this)
        btn_change_email.setOnClickListener(this)
    }

    private fun validateForm(): Boolean {
        val email = et_email_setting.text.toString().trim()
        var isValid = true

        if (TextUtils.isEmpty(email)) {
            til_email_setting.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            til_email_setting.error = "Invalid email"
            isValid = false
        }

        return isValid
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ic_back_button_toolbar -> {
                onBackPressed()
            }
            R.id.btn_change_email -> {
                if (validateForm()) {
                    til_email_setting.error = null
                    et_email_setting.clearFocus()

                    updateEmail(et_email_setting.text.toString().lowercase())
                }
            }
        }
    }

    private fun updateEmail(emailUpdate: String) {
        dialogLoading.show(supportFragmentManager, "loading")

        val client = ApiConfig.getApiService().updateEmail(
            authPreference.getToken(),
            authPreference.getUser().id.toString(),
            emailUpdate,
            emailUpdate
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
                        authPreference.updateEmail(emailUpdate)

                        val intent = Intent(
                            this@EmailSettingActivity,
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
                            this@EmailSettingActivity,
                            jObjError.getJSONObject("error").getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@EmailSettingActivity, e.message, Toast.LENGTH_LONG)
                            .show()
                    }
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UpdateUserResponse>, t: Throwable) {
                dialogLoading.stop()

                Toast.makeText(
                    this@EmailSettingActivity,
                    "Error. Please contact developer",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }
}