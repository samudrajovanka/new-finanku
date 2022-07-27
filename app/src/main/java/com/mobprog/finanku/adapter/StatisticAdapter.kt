package com.mobprog.finanku.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.mobprog.finanku.R
import com.mobprog.finanku.model.CategoryExpenses
import com.mobprog.finanku.data.Statistic
import com.mobprog.finanku.utils.toCurrencyIDR
import kotlinx.android.synthetic.main.item_card_statistic.view.*

class StatisticAdapter(var context: Context, var arrayList: ArrayList<Statistic>) : BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(position: Int): Any {
        return arrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View? {
        var convertView = view

        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        if (convertView == null) {
            convertView = layoutInflater?.inflate(R.layout.item_card_statistic, null)
        }

        val statistic = arrayList[position]
        convertView?.pb_item_statistic?.progress = statistic.getPercent().toFloat()
        convertView?.tv_spent_percent_item_statistic?.text = "${statistic.getPercent()}%"
        convertView?.tv_category_expenses_item_statistic?.text = statistic.category.type
        convertView?.tv_spent_category?.text = statistic.spent.toCurrencyIDR()
        convertView?.tv_limit_category?.text = statistic.limit.toCurrencyIDR(false)
        convertView?.tv_status_item_statistic?.text = Statistic.getStatus(statistic.getPercent())

        when (statistic.category) {
            CategoryExpenses.FOOD -> convertView?.iv_category_item_statistic?.setImageResource(R.drawable.ic_food)
            CategoryExpenses.SHOP -> convertView?.iv_category_item_statistic?.setImageResource(R.drawable.ic_shop)
            CategoryExpenses.TRAVEL -> convertView?.iv_category_item_statistic?.setImageResource(R.drawable.ic_travel)
            CategoryExpenses.OTHER -> convertView?.iv_category_item_statistic?.setImageResource(R.drawable.ic_others)
        }

        when {
            statistic.getPercent() > 80 -> {
                val redColor = ContextCompat.getColor(context, R.color.red)
                changeColorStatus(convertView, redColor)
            }
            statistic.getPercent() > 50 -> {
                val yellowColor = ContextCompat.getColor(context, R.color.yellow)
                changeColorStatus(convertView, yellowColor)
            }
            else -> {
                val greenColor = ContextCompat.getColor(context, R.color.green)
                changeColorStatus(convertView, greenColor)
            }
        }

        return convertView
    }

    private fun changeColorStatus(view: View?, color: Int) {
        view?.pb_item_statistic?.progressBarColor = color
        view?.tv_spent_percent_item_statistic?.setTextColor(color)
        view?.tv_status_item_statistic?.setTextColor(color)
    }
}