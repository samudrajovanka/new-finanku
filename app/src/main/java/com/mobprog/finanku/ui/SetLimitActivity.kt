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
import com.mobprog.finanku.data.LimitPostResponse
import com.mobprog.finanku.network.ApiConfig
import com.mobprog.finanku.preference.AuthPreference
import com.mobprog.finanku.preference.LimitPreference
import com.mobprog.finanku.utils.CustomTextWatcher
import com.mobprog.finanku.utils.toCurrencyIDR
import com.mobprog.finanku.view.DialogLoading
import kotlinx.android.synthetic.main.activity_set_limit.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetLimitActivity : AppCompatActivity(), View.OnClickListener {
    private val dialogLoading by lazy { DialogLoading() }
    private var totalLimit = 0
    private var limitFood = 0
    private var limitShop = 0
    private var limitTravel = 0
    private var limitOthers = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_limit)

        initiateUi()
    }

    private val limitTextWatcher = object : CustomTextWatcher() {
        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            super.onTextChanged(text, p1, p2, p3)

            limitFood = if (TextUtils.isEmpty(et_set_limit_food.text.toString())) {
                0
            } else {
                et_set_limit_food.text.toString().toInt()
            }

            limitShop = if (TextUtils.isEmpty(et_set_limit_shop.text.toString())) {
                0
            } else {
                et_set_limit_shop.text.toString().toInt()
            }

            limitTravel = if (TextUtils.isEmpty(et_set_limit_travel.text.toString())) {
                0
            } else {
                et_set_limit_travel.text.toString().toInt()
            }

            limitOthers = if (TextUtils.isEmpty(et_set_limit_others.text.toString())) {
                0
            } else {
                et_set_limit_others.text.toString().toInt()
            }

            totalLimit = limitFood + limitShop + limitTravel + limitOthers
            tv_total_limit.text = totalLimit.toCurrencyIDR()
        }
    }

    private fun initiateUi() {
        et_set_limit_food.addTextChangedListener(limitTextWatcher)
        et_set_limit_shop.addTextChangedListener(limitTextWatcher)
        et_set_limit_travel.addTextChangedListener(limitTextWatcher)
        et_set_limit_others.addTextChangedListener(limitTextWatcher)

        tv_total_limit.text = totalLimit.toCurrencyIDR()

        btn_continue_set_limit.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_continue_set_limit -> {
                et_set_limit_food.clearFocus()
                et_set_limit_shop.clearFocus()
                et_set_limit_travel.clearFocus()
                et_set_limit_others.clearFocus()
                setLimit(limitFood, limitShop, limitTravel, limitOthers, totalLimit)
            }
        }
    }

    private fun setLimit(
        limitFood: Int,
        limitShop: Int,
        limitTravel: Int,
        limitOthers: Int,
        total: Int
    ) {
        dialogLoading.show(supportFragmentManager, "loading")

        val authPreference = AuthPreference(this)

        val client = ApiConfig.getApiService().setLimits(
            authPreference.getToken(),
            limitFood,
            limitShop,
            limitTravel,
            limitOthers,
            total,
            authPreference.getUser().id
        )
        client.enqueue(object : Callback<LimitPostResponse> {
            override fun onResponse(
                call: Call<LimitPostResponse>,
                response: Response<LimitPostResponse>
            ) {
                dialogLoading.stop()

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val limitPreference = LimitPreference(this@SetLimitActivity)
                        limitPreference.setLimit(
                            responseBody.data.id,
                            limitFood,
                            limitShop,
                            limitTravel,
                            limitOthers,
                            total
                        )

                        val intent = Intent(this@SetLimitActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()?.string())
                        Toast.makeText(
                            this@SetLimitActivity,
                            jObjError.getJSONObject("error").getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@SetLimitActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LimitPostResponse>, t: Throwable) {
                dialogLoading.stop()

                Toast.makeText(
                    this@SetLimitActivity,
                    "Error. Please contact developer",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }

        })
    }
}