package com.mobprog.finanku.ui

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mobprog.finanku.R
import com.mobprog.finanku.data.AuthResponse
import com.mobprog.finanku.network.ApiConfig
import com.mobprog.finanku.preference.AuthPreference
import com.mobprog.finanku.preference.ExpensesPreference
import com.mobprog.finanku.utils.CustomTextWatcher
import com.mobprog.finanku.utils.onChangeRemoveError
import com.mobprog.finanku.view.DialogLoading
import kotlinx.android.synthetic.main.activity_password_setting.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity(), View.OnClickListener {
    private val dialogLoading by lazy { DialogLoading() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initiateUi()
    }

    private val emailTextWatcher = object : CustomTextWatcher() {
        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            super.onTextChanged(text, p1, p2, p3)

            val email = et_sign_up_email.text.toString()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                til_sign_up_email.error = "Invalid email"
            } else {
                til_sign_up_email.error = null
            }
        }
    }

    private val passwordTextWatcher = object : CustomTextWatcher() {
        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            super.onTextChanged(text, p1, p2, p3)

            val password = et_sign_up_password.text.toString()
            if (password.length < 8) {
                til_sign_up_password.error = getString(R.string.password_min_error)
            } else {
                til_sign_up_password.error = null
            }
        }
    }

    private fun initiateUi() {
        et_sign_up_full_name.addTextChangedListener(onChangeRemoveError(til_sign_up_full_name))
        et_sign_up_email.addTextChangedListener(emailTextWatcher)
        et_sign_up_password.addTextChangedListener(passwordTextWatcher)
        et_sign_up_confirm_password.addTextChangedListener(
            onChangeRemoveError(
                til_sign_up_confirm_password
            )
        )

        btn_sign_up.setOnClickListener(this)
        tv_to_sign_in.setOnClickListener(this)
    }

    private fun validateInput(): Boolean {
        val fullName = et_sign_up_full_name.text.toString()
        val email = et_sign_up_email.text.toString()
        val password = et_sign_up_password.text.toString()
        val confirmPassword = et_sign_up_confirm_password.text.toString()
        var isValid = true

        if (TextUtils.isEmpty(fullName)) {
            til_sign_up_full_name.error = "Full Name is required"
            isValid = false
        }

        if (TextUtils.isEmpty(email)) {
            til_sign_up_email.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            til_sign_up_email.error = "Invalid email"
            isValid = false
        }

        if (TextUtils.isEmpty(password)) {
            til_sign_up_password.error = "Password is required"
            isValid = false
        } else if (password.length < 8) {
            til_sign_up_password.error = getString(R.string.password_min_error)
            isValid = false
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            til_sign_up_confirm_password.error = "Confirm password is required"
            isValid = false
        } else if (confirmPassword != password) {
            til_sign_up_confirm_password.error = "Confirm password didn't match"
            isValid = false
        }

        return isValid
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_sign_up -> {
                if (validateInput()) {
                    til_sign_up_full_name.error = null
                    til_sign_up_email.error = null
                    til_sign_up_password.error = null
                    til_sign_up_confirm_password.error = null

                    et_sign_up_full_name.clearFocus()
                    et_sign_up_email.clearFocus()
                    et_sign_up_password.clearFocus()
                    et_sign_up_confirm_password.clearFocus()

                    val fullName = et_sign_up_full_name.text.toString()
                    val email = et_sign_up_email.text.toString()
                    val password = et_sign_up_password.text.toString()

                    register(fullName, email.toLowerCase(), password)
                }
            }
            R.id.tv_to_sign_in -> {
                val intent = Intent(this, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }

    private fun register(name: String, email: String, password: String) {
        dialogLoading.show(supportFragmentManager, "loading")

        val client = ApiConfig.getApiService().register(name, email, email, password)
        client.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                dialogLoading.stop()

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val authPreference = AuthPreference(this@SignUpActivity)
                        authPreference.login(responseBody.jwt, responseBody.user)

                        val expensesPreference = ExpensesPreference(this@SignUpActivity)
                        expensesPreference.setExpenses(0, 0, 0, 0, 0)

                        val intent = Intent(this@SignUpActivity, SetLimitActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()?.string())
                        Toast.makeText(
                            this@SignUpActivity,
                            jObjError.getJSONObject("error").getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@SignUpActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                dialogLoading.stop()

                Toast.makeText(
                    this@SignUpActivity,
                    "Error. Please contact developer",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}