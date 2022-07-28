package com.mobprog.finanku.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobprog.finanku.R
import com.mobprog.finanku.adapter.StatisticAdapter
import com.mobprog.finanku.data.Data
import com.mobprog.finanku.preference.ExpensesPreference
import com.mobprog.finanku.preference.LimitPreference
import com.mobprog.finanku.utils.toCurrencyIDR
import kotlinx.android.synthetic.main.fragment_statistic.*
import kotlinx.android.synthetic.main.layout_total_balance.*

class StatisticFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initiateUi()
    }

    private fun initiateUi() {
        setAdapterGrid()

        val limitPreference = context?.let { LimitPreference(it) }
        val expensesPreference = context?.let { ExpensesPreference(it) }

        tv_total_spent.text = expensesPreference?.getAllTotal()?.toCurrencyIDR()
        tv_limit_amount.text = limitPreference?.getLimit()?.total?.toCurrencyIDR(false)
    }

    private fun setAdapterGrid() {
        val arrayList = Data.getStatistics(requireContext())
        val statisticAdapter = StatisticAdapter(requireActivity(), arrayList)
        grid_view_statistic.adapter = statisticAdapter
    }
}