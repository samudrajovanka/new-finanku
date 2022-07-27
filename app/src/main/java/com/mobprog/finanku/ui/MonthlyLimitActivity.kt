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
import com.mobprog.finanku.data.LimitPutResponse
import com.mobprog.finanku.listener.DialogAlertListener
import com.mobprog.finanku.network.ApiConfig
import com.mobprog.finanku.preference.AuthPreference
import com.mobprog.finanku.preference.LimitPreference
import com.mobprog.finanku.utils.CustomTextWatcher
import com.mobprog.finanku.utils.toCurrencyIDR
import com.mobprog.finanku.view.DialogAlertFragment
import com.mobprog.finanku.view.DialogLoading
import kotlinx.android.synthetic.main.activity_monthly_limit.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MonthlyLimitActivity : AppCompatActivity(), View.OnClickListener, DialogAlertListener {
    private val dialogLoading by lazy { DialogLoading() }
    private var idLimit = 0
    private var totalLimit = 0
    private var limitFood = 0
    private var limitShop = 0
    private var limitTravel = 0
    private var limitOthers = 0

    private val dialogAlert by lazy { DialogAlertFragment(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_limit)

        initiateUi()
    }

    private val limitTextWatcher = object : CustomTextWatcher() {
        override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
            super.onTextChanged(text, p1, p2, p3)

            limitFood = if (TextUtils.isEmpty(et_monthly_limit_food.text.toString())) {
                0
            } else {
                et_monthly_limit_food.text.toString().toInt()
            }

            limitShop = if (TextUtils.isEmpty(et_monthly_limit_shop.text.toString())) {
                0
            } else {
                et_monthly_limit_shop.text.toString().toInt()
            }

            limitTravel = if (TextUtils.isEmpty(et_monthly_limit_travel.text.toString())) {
                0
            } else {
                et_monthly_limit_travel.text.toString().toInt()
            }

            limitOthers = if (TextUtils.isEmpty(et_monthly_limit_others.text.toString())) {
                0
            } else {
                et_monthly_limit_others.text.toString().toInt()
            }

            totalLimit = limitFood + limitShop + limitTravel + limitOthers
            tv_total_monthly_limit.text = totalLimit?.toCurrencyIDR()
        }
    }

    private fun initiateUi() {
        tv_title_toolbar.text = getString(R.string.monthly_limit_title)

        initUserLimit()

        et_monthly_limit_food.addTextChangedListener(limitTextWatcher)
        et_monthly_limit_shop.addTextChangedListener(limitTextWatcher)
        et_monthly_limit_travel.addTextChangedListener(limitTextWatcher)
        et_monthly_limit_others.addTextChangedListener(limitTextWatcher)

        ic_back_button_toolbar.setOnClickListener(this)
        btn_update_limit.setOnClickListener(this)
    }

    private fun initUserLimit() {
        val limitPreference = LimitPreference(this)

        val limit = limitPreference.getLimit()
        limitFood = limit.food
        limitShop = limit.shop
        limitTravel = limit.travel
        limitOthers = limit.others
        idLimit = limit.id

        et_monthly_limit_food.setText(limitPreference.getLimit().food.toString())
        et_monthly_limit_shop.setText(limitPreference.getLimit().shop.toString())
        et_monthly_limit_travel.setText(limitPreference.getLimit().travel.toString())
        et_monthly_limit_others.setText(limitPreference.getLimit().others.toString())

        totalLimit = limitPreference.getLimit().total
        tv_total_monthly_limit.text = totalLimit?.toCurrencyIDR()
    }

    private fun updateLimit(id: Int, limitFood: Int, limitShop: Int, limitTravel: Int, limitOthers: Int, total: Int) {
        dialogLoading.show(supportFragmentManager, "loading")

        val authPreference = AuthPreference(this)

        val client = ApiConfig.getApiService().updateLimits(
            authPreference.getToken(),
            id,
            limitFood,
            limitShop,
            limitTravel,
            limitOthers,
            total,
        )
        client.enqueue(object : Callback<LimitPutResponse> {
            override fun onResponse(
                call: Call<LimitPutResponse>,
                response: Response<LimitPutResponse>
            ) {
                dialogLoading.stop()

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val limitPreference = LimitPreference(this@MonthlyLimitActivity)
                        limitPreference.setLimit(
                            responseBody.data.id,
                            limitFood,
                            limitShop,
                            limitTravel,
                            limitOthers,
                            total
                        )

                        dialogAlert.setMessage("Update successfully")
                        dialogAlert.show(supportFragmentManager, "Alert success update monthly limit")
                    }
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()?.string())
                        Toast.makeText(
                            this@MonthlyLimitActivity,
                            jObjError.getJSONObject("error").getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@MonthlyLimitActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LimitPutResponse>, t: Throwable) {
                dialogLoading.stop()

                Toast.makeText(
                    this@MonthlyLimitActivity,
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
            R.id.btn_update_limit -> {
                updateLimit(idLimit, limitFood, limitShop, limitTravel, limitOthers, totalLimit)
            }
        }
    }

    override fun action() {
        onBackPressed()
    }
}