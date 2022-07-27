package com.mobprog.finanku.data

import com.google.gson.annotations.SerializedName

data class CategoryGetResponse(
	val data: ArrayList<CategoryResponse>,
)

data class CategoryResponse(
	@SerializedName("attributes")
	val category: Category? = null,
	val id: Int? = 0,
)

