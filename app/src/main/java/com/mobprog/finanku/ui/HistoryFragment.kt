package com.mobprog.finanku.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.mobprog.finanku.R
import com.mobprog.finanku.adapter.ViewPagerAdapter
import com.mobprog.finanku.utils.dateNow
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.item_time_now_active.*

class HistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initiateUi()
    }

    private fun initiateUi() {
        setAdapterViewPager()

        tv_history_current_date.text = dateNow()
    }

    private fun setAdapterViewPager() {
        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        adapter.addFragment(CurrentMonthFragment())
        adapter.addFragment(LastMonthFragment())

        vp_history.adapter = adapter

        val tabText = arrayOf(
            getString(R.string.current_month),
            getString(R.string.last_month)
        )

        TabLayoutMediator(tl_history, vp_history) { tab, position ->
            tab.text = tabText[position]
        }.attach()
    }
}