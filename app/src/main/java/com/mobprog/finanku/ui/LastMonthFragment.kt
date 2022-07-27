package com.mobprog.finanku.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobprog.finanku.R
import com.mobprog.finanku.adapter.StatisticAdapter
import com.mobprog.finanku.data.Data
import com.mobprog.finanku.utils.previousMonth
import kotlinx.android.synthetic.main.fragment_last_month.*

class LastMonthFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_last_month, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initiateUi()
    }

    private fun initiateUi() {
        setAdapterGridStatistic()

        tv_title_last_month.text = String.format("Expenses of %s", previousMonth())
    }

    private fun setAdapterGridStatistic() {
        val arrayList = Data.getLastStatistic()
        val statisticAdapter = StatisticAdapter(requireActivity(), arrayList)
        grid_statistic_last_month.adapter = statisticAdapter
    }
}