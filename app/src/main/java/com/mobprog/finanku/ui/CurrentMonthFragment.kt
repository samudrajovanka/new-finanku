package com.mobprog.finanku.ui

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobprog.finanku.R
import com.mobprog.finanku.adapter.HistoryAdapter
import com.mobprog.finanku.data.ExpensesGetResponse
import com.mobprog.finanku.data.ExpensesResponse
import com.mobprog.finanku.network.ApiConfig
import com.mobprog.finanku.preference.AuthPreference
import kotlinx.android.synthetic.main.fragment_current_month.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrentMonthFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_current_month, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initiateUi()
    }

    private fun initiateUi() {
        getExpenses()
    }

    private fun setAdapterList(histories: ArrayList<ExpensesResponse>) {
        val historyAdapter = HistoryAdapter(histories)

        rv_current_month?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
        }

        rv_current_month?.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
    }

    private fun showList(histories: ArrayList<ExpensesResponse>) {
        if (histories.size == 0) {
            iv_no_data_current_month?.visibility = View.VISIBLE
        } else {
            iv_no_data_current_month?.visibility = View.GONE
            setAdapterList(histories)
        }
    }

    private fun showLoading(isShow: Boolean) {
        if (isShow) {
            pb_history_current_month.visibility = View.VISIBLE
        } else {
            pb_history_current_month?.visibility = View.GONE
        }
    }

    private fun getExpenses() {
        showLoading(true)

        val authPreference = AuthPreference(requireActivity())

        val client = ApiConfig.getApiService().getExpensesByUserId(
            authPreference.getToken(),
            authPreference.getUser().id.toString()
        )
        client.enqueue(object : Callback<ExpensesGetResponse> {
            override fun onResponse(
                call: Call<ExpensesGetResponse>,
                response: Response<ExpensesGetResponse>
            ) {
                showLoading(false)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        showList(responseBody.data)
                    }
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()?.string())
                        Toast.makeText(
                            activity,
                            jObjError.getJSONObject("error").getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                    }
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ExpensesGetResponse>, t: Throwable) {
                showLoading(false)

                Toast.makeText(
                    activity,
                    "Error. Please contact developer",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }

        })
    }
}