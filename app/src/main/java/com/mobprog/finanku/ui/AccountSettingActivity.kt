package com.mobprog.finanku.ui

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.mobprog.finanku.R
import com.mobprog.finanku.data.DeleteUserResponse
import com.mobprog.finanku.data.LimitDeleteResponse
import com.mobprog.finanku.data.UpdateUserResponse
import com.mobprog.finanku.listener.DialogItemListener
import com.mobprog.finanku.network.ApiConfig
import com.mobprog.finanku.preference.AuthPreference
import com.mobprog.finanku.preference.ExpensesPreference
import com.mobprog.finanku.preference.LimitPreference
import com.mobprog.finanku.view.DialogLoading
import com.mobprog.finanku.view.DialogPromptFragment
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountSettingActivity : AppCompatActivity(), View.OnClickListener, DialogItemListener {

    private val dialogPrompt by lazy { DialogPromptFragment(this) }
    private val dialogLoading by lazy { DialogLoading() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)

        initiateUi()
    }

    private fun initiateUi() {
        setProfile()

        ic_back_button_toolbar.setOnClickListener(this)
        cl_email_account_setting.setOnClickListener(this)
        cl_change_password_account_setting.setOnClickListener(this)
        cl_delete_account_setting.setOnClickListener(this)
    }

    private fun setProfile() {
        val authPreference = AuthPreference(this)

        tv_real_name_account_setting.text = authPreference.getUser().name
        tv_email_account_setting.text = authPreference.getUser().getPrivateEmail()
    }

    private fun deleteLimit() {
        dialogLoading.show(supportFragmentManager, "loading")

        val authPreference = AuthPreference(this@AccountSettingActivity)
        val limitPreference = LimitPreference(this@AccountSettingActivity)

        val client = ApiConfig.getApiService().deleteLimit(
            authPreference.getToken(),
            limitPreference.getLimit().id,
        )

        client.enqueue(object : Callback<LimitDeleteResponse> {
            override fun onResponse(
                call: Call<LimitDeleteResponse>,
                response: Response<LimitDeleteResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        deleteAccount()
                    }
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()?.string())
                        Toast.makeText(
                            this@AccountSettingActivity,
                            jObjError.getJSONObject("error").getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@AccountSettingActivity, e.message, Toast.LENGTH_LONG)
                            .show()
                    }
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LimitDeleteResponse>, t: Throwable) {
                dialogLoading.stop()

                Toast.makeText(
                    this@AccountSettingActivity,
                    "Error. Please contact developer",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun deleteAccount() {
        val authPreference = AuthPreference(this@AccountSettingActivity)
        val limitPreference = LimitPreference(this@AccountSettingActivity)
        val expensesPreference = ExpensesPreference(this@AccountSettingActivity)
        val client = ApiConfig.getApiService().deleteUser(
            authPreference.getToken(),
            authPreference.getUser().id.toString(),
        )

        client.enqueue(object : Callback<DeleteUserResponse> {
            override fun onResponse(
                call: Call<DeleteUserResponse>,
                response: Response<DeleteUserResponse>
            ) {
                dialogLoading.stop()

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        authPreference.logout()
                        limitPreference.clearLimit()
                        expensesPreference.reset()

                        val intent = Intent(this@AccountSettingActivity, SignInActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()?.string())
                        Toast.makeText(
                            this@AccountSettingActivity,
                            jObjError.getJSONObject("error").getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@AccountSettingActivity, e.message, Toast.LENGTH_LONG)
                            .show()
                    }
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DeleteUserResponse>, t: Throwable) {
                dialogLoading.stop()

                Toast.makeText(
                    this@AccountSettingActivity,
                    "Error. Please contact developer",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ic_back_button_toolbar -> {
                onBackPressed()
            }
            R.id.cl_email_account_setting -> {
                startActivity(Intent(this, EmailSettingActivity::class.java))
            }
            R.id.cl_change_password_account_setting -> {
                startActivity(Intent(this, PasswordSettingActivity::class.java))
            }
            R.id.cl_delete_account_setting -> {
                dialogPrompt.setMessage("Are you sure want to delete your account?")
                dialogPrompt.setTextPositiveBtn("Cancel")
                dialogPrompt.setTextNegativeBtn("Yes")
                dialogPrompt.show(supportFragmentManager, "Prompt delete account")
            }
        }
    }

    override fun getUserChoice(yes: Boolean) {
        if (!yes) {
            deleteLimit()
        }
    }
}