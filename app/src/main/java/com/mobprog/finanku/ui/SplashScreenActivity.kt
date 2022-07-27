package com.mobprog.finanku.ui

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.mobprog.finanku.data.CategoryGetResponse
import com.mobprog.finanku.data.Data
import com.mobprog.finanku.network.ApiConfig
import com.mobprog.finanku.preference.AuthPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getCategories()
    }

    private fun getCategories() {
        val client = ApiConfig.getApiService().getCategories()
        client.enqueue(object : Callback<CategoryGetResponse> {
            override fun onResponse(
                call: Call<CategoryGetResponse>,
                response: Response<CategoryGetResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Data.categories.addAll(responseBody.data)

                        val authPreference = AuthPreference(this@SplashScreenActivity)
                        val isLogin = authPreference.isLogin()

                        if (isLogin) {
                            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                            finish()
                        } else {
                            startActivity(Intent(this@SplashScreenActivity, SignInActivity::class.java))
                            finish()
                        }
                    }
                } else {

                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CategoryGetResponse>, t: Throwable) {


                Toast.makeText(
                    this@SplashScreenActivity,
                    "Error. Please contact developer",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }
}