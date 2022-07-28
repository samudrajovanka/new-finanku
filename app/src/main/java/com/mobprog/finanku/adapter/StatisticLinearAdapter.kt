package com.mobprog.finanku.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mobprog.finanku.R
import com.mobprog.finanku.data.Statistic
import kotlinx.android.synthetic.main.item_linear_statistic.view.*

class StatisticLinearAdapter(private val statistics: ArrayList<Statistic>) :
    RecyclerView.Adapter<StatisticLinearAdapter.StatisticLinearHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StatisticLinearHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_linear_statistic, parent, false)
        return StatisticLinearHolder(view)
    }

    override fun getItemCount(): Int = statistics.size

    override fun onBindViewHolder(
        holder: StatisticLinearHolder,
        position: Int
    ) {
        holder.bindHistory(statistics[position])
    }

    inner class StatisticLinearHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTitle = view.tv_item_title_statistic_linear
        private val pbPercentage = view.pb_item_statistic_linear
        private val context = view.context

        fun bindHistory(statistic: Statistic) {
            tvTitle.text = statistic.category.type
            pbPercentage.progress = statistic.getPercent()

            when {
                statistic.getPercent() > 80 -> {
                    val redColor = ContextCompat.getColor(context, R.color.red)
                    pbPercentage.setIndicatorColor(redColor)
                }
                statistic.getPercent() > 50 -> {
                    val yellowColor = ContextCompat.getColor(context, R.color.yellow)
                    pbPercentage.setIndicatorColor(yellowColor)
                }
                else -> {
                    val greenColor = ContextCompat.getColor(context, R.color.green)
                    pbPercentage.setIndicatorColor(greenColor)
                }
            }
        }

    }
}