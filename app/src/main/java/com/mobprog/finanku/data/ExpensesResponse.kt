package com.mobprog.finanku.data

import com.google.gson.annotations.SerializedName

data class ExpensesPostResponse(
	val data: ExpensesResponse,
)

data class ExpensesGetResponse(
	val data: ArrayList<ExpensesResponse>
) {
	fun getTotalFood(): Int {
		return this.data.fold(0, { total, item ->
			if (item.expenses.type.data.category?.type == "Food") {
				total + item.expenses.amount
			} else total
		})
	}

	fun getTotalShop(): Int {
		return this.data.fold(0, { total, item ->
			if (item.expenses.type.data.category?.type == "Shop") {
				total + item.expenses.amount
			} else total
		})
	}

	fun getTotalTravel(): Int {
		return this.data.fold(0, { total, item ->
			if (item.expenses.type.data.category?.type == "Travel") {
				total + item.expenses.amount
			} else total
		})
	}

	fun getTotalOthers(): Int {
		return this.data.fold(0, { total, item ->
			if (item.expenses.type.data.category?.type == "Others") {
				total + item.expenses.amount
			} else total
		})
	}

	fun getAllTotal(): Int {
		return this.data.fold(0, { total, item -> total + item.expenses.amount })
	}
}

data class ExpensesResponse(
	@SerializedName("attributes")
	val expenses: Expenses,
	val id: Int,
)
