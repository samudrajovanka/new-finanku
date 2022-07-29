package com.mobprog.finanku.ui

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.mobprog.finanku.R
import com.mobprog.finanku.data.*
import com.mobprog.finanku.network.ApiConfig
import com.mobprog.finanku.preference.AuthPreference
import com.mobprog.finanku.preference.ExpensesPreference
import com.mobprog.finanku.preference.LimitPreference
import com.mobprog.finanku.utils.onChangeRemoveError
import com.mobprog.finanku.view.DialogLoading
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SignInActivity : AppCompatActivity(), View.OnClickListener {
    private val dialogLoading by lazy { DialogLoading() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        initiateUi()
    }

    private fun initiateUi() {
        et_sign_in_email.addTextChangedListener(onChangeRemoveError(til_sign_in_email))
        et_sign_in_password.addTextChangedListener(onChangeRemoveError(til_sign_in_password))

        btn_sign_in.setOnClickListener(this)
        tv_to_sign_up.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_sign_in -> {
                val emailInput = et_sign_in_email.text.toString()
                val passwordInput = et_sign_in_password.text.toString()
                var isValid = true

                if (TextUtils.isEmpty(emailInput)) {
                    til_sign_in_email.error = "Email is required"
                    isValid = false
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                    til_sign_in_email.error = "Invalid email"
                    isValid = false
                }

                if (TextUtils.isEmpty(passwordInput)) {
                    til_sign_in_password.error = "Password is required"
                    isValid = false
                }

                if (isValid) {
                    til_sign_in_email.error = null
                    til_sign_in_password.error = null

                    et_sign_in_email.clearFocus()
                    et_sign_in_password.clearFocus()

                    login(emailInput.lowercase(Locale.getDefault()), passwordInput)
                }
            }
            R.id.tv_to_sign_up -> {
                startActivity(Intent(this, SignUpActivity::class.java))
            }
        }
    }

    private fun login(email: String, password: String) {
        dialogLoading.show(supportFragmentManager, "loading")

        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val authPreference = AuthPreference(this@SignInActivity)
                        authPreference.login(responseBody.jwt, responseBody.user)

                        getLimit("Bearer ${responseBody.jwt}", responseBody.user.id.toString())
                    }
                } else {
                    dialogLoading.stop()

                    Toast.makeText(
                        this@SignInActivity,
                        "Email or password is wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                dialogLoading.stop()

                Toast.makeText(
                    this@SignInActivity,
                    "Error. Please contact developer",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getLimit(token: String, userId: String) {
        val client = ApiConfig.getApiService().getLimitsByUserId(token, userId)
        client.enqueue(object : Callback<LimitGetResponse> {
            override fun onResponse(
                call: Call<LimitGetResponse>,
                response: Response<LimitGetResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val limitPreference = LimitPreference(this@SignInActivity)

                        val idLimit = responseBody.data[0].id
                        val food = responseBody.data[0].limit.food
                        val shop = responseBody.data[0].limit.shop
                        val travel = responseBody.data[0].limit.travel
                        val others = responseBody.data[0].limit.others
                        val total = responseBody.data[0].limit.total

                        if (idLimit != null && food != null && shop != null && travel != null && others != null && total != null) {
                            limitPreference.setLimit(
                                idLimit,
                                food,
                                shop,
                                travel,
                                others,
                                total
                            )
                        }

                        getExpenses()
                    }
                } else {
                    val authPreference = AuthPreference(this@SignInActivity)
                    authPreference.logout()

                    try {
                        val jObjError = JSONObject(response.errorBody()?.string())
                        Toast.makeText(
                            this@SignInActivity,
                            jObjError.getJSONObject("error").getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@SignInActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LimitGetResponse>, t: Throwable) {
                dialogLoading.stop()

                val authPreference = AuthPreference(this@SignInActivity)
                authPreference.logout()

                Toast.makeText(
                    this@SignInActivity,
                    "Error. Please contact developer",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getExpenses() {
        val authPreference = AuthPreference(this)

        val client = ApiConfig.getApiService().getExpensesByUserId(
            authPreference.getToken(),
            authPreference.getUser().id.toString()
        )
        client.enqueue(object : Callback<ExpensesGetResponse> {
            override fun onResponse(
                call: Call<ExpensesGetResponse>,
                response: Response<ExpensesGetResponse>
            ) {
                dialogLoading.stop()
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val expensesPreference = ExpensesPreference(this@SignInActivity)
                        val food = responseBody.getTotalFood()
                        val shop = responseBody.getTotalShop()
                        val travel = responseBody.getTotalTravel()
                        val others = responseBody.getTotalOthers()
                        val total = responseBody.getAllTotal()

                        expensesPreference.setExpenses(food, shop, travel, others, total)

                        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                } else {
                    val authPreference = AuthPreference(this@SignInActivity)
                    authPreference.logout()

                    try {
                        val jObjError = JSONObject(response.errorBody()?.string())
                        Toast.makeText(
                            this@SignInActivity,
                            jObjError.getJSONObject("error").getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@SignInActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ExpensesGetResponse>, t: Throwable) {
                dialogLoading.stop()

                val authPreference = AuthPreference(this@SignInActivity)
                authPreference.logout()

                Toast.makeText(
                    this@SignInActivity,
                    "Error. Please contact developer",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }

        })
    }
}