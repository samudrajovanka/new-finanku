package com.mobprog.finanku.ui

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobprog.finanku.R
import com.mobprog.finanku.adapter.HistoryAdapter
import com.mobprog.finanku.adapter.StatisticLinearAdapter
import com.mobprog.finanku.data.Data
import com.mobprog.finanku.data.ExpensesGetResponse
import com.mobprog.finanku.data.ExpensesResponse
import com.mobprog.finanku.network.ApiConfig
import com.mobprog.finanku.preference.AuthPreference
import com.mobprog.finanku.preference.ExpensesPreference
import com.mobprog.finanku.preference.LimitPreference
import com.mobprog.finanku.utils.OffsetDecoration
import com.mobprog.finanku.utils.dateNow
import com.mobprog.finanku.utils.toCurrencyIDR
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_time_now_active.*
import kotlinx.android.synthetic.main.layout_total_balance.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initiateUi()
    }

    private fun initiateUi() {
        getExpenses()
        setAdapterListStatistic()

        setProfile()
        tv_history_current_date.text = dateNow()

        btn_add_expense_home.setOnClickListener(this)
        btn_see_all_history.setOnClickListener(this)
    }

    private fun setProfile() {
        val authPreference = context?.let { AuthPreference(it) }
        val limitPreference = context?.let { LimitPreference(it) }
        val expensesPreference = context?.let { ExpensesPreference(it) }

        tv_title_greeting_name.text = authPreference?.getUser()?.getFirstName()

        tv_total_spent.text = expensesPreference?.getAllTotal()?.toCurrencyIDR()
        tv_limit_amount.text = limitPreference?.getLimit()?.total?.toCurrencyIDR(false)
    }

    private fun setAdapterListHistory(histories: ArrayList<ExpensesResponse>) {
        val listHistories = histories.filterIndexed { idx, _ -> idx < 10 }


        val historyAdapter = HistoryAdapter(ArrayList(listHistories))

        rv_latest_history_home?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
        }

        rv_latest_history_home?.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
    }

    private fun setAdapterListStatistic() {
        val listStatistic = Data.getStatistics(requireActivity())

        val statisticLinearAdapter = StatisticLinearAdapter(listStatistic)

        rv_statistic_linear_home.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = statisticLinearAdapter
        }

        rv_statistic_linear_home.addItemDecoration(
            OffsetDecoration(requireContext(), top = 12)
        )
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_add_expense_home -> {
                findNavController().navigate(R.id.navigation_add_expenses)
            }
            R.id.btn_see_all_history -> {
                findNavController().navigate(R.id.navigation_history)
            }
        }
    }

    private fun showListHistory(histories: ArrayList<ExpensesResponse>) {
        if (histories.size == 0) {
            iv_no_data_home?.visibility = View.VISIBLE
        } else {
            iv_no_data_home?.visibility = View.GONE
            rv_latest_history_home?.visibility = View.VISIBLE
            btn_see_all_history?.visibility = View.VISIBLE
            setAdapterListHistory(histories)
        }
    }

    private fun showLoading(isShow: Boolean) {
        if (isShow) {
            pb_home.visibility = View.VISIBLE
            rv_latest_history_home?.visibility = View.GONE
            btn_see_all_history?.visibility = View.GONE
        } else {
            pb_home?.visibility = View.GONE
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
                        showListHistory(responseBody.data)
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