package com.mobprog.finanku.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobprog.finanku.R
import com.mobprog.finanku.data.ExpensesResponse
import com.mobprog.finanku.model.History
import com.mobprog.finanku.model.CategoryExpenses
import com.mobprog.finanku.utils.formatDate
import com.mobprog.finanku.utils.toCurrencyIDR
import kotlinx.android.synthetic.main.item_history.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryAdapter(private val histories: ArrayList<ExpensesResponse>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryHolder(view)
    }

    override fun getItemCount(): Int = histories.size

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        holder.bindHistory(histories[position])
    }

    class HistoryHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvDate = view.tv_date_item_history
        private val tvDescription = view.tv_desc_item_history
        private val tvAmount = view.tv_amount_item_history
        private val ivIcon = view.iv_icon_item_history

        fun bindHistory(history: ExpensesResponse) {
            tvDate.text = formatDate(history.expenses.date)
            tvDescription.text = history.expenses.description
            tvAmount.text = history.expenses.amount.toCurrencyIDR()

            when (history.expenses.type.data.category?.type) {
                CategoryExpenses.FOOD.type -> ivIcon.setImageResource(R.drawable.ic_food)
                CategoryExpenses.SHOP.type -> ivIcon.setImageResource(R.drawable.ic_shop)
                CategoryExpenses.TRAVEL.type -> ivIcon.setImageResource(R.drawable.ic_travel)
                CategoryExpenses.OTHER.type -> ivIcon.setImageResource(R.drawable.ic_other)
            }
        }
    }
}