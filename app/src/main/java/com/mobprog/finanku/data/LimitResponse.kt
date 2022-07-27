package com.mobprog.finanku.data

import com.google.gson.annotations.SerializedName

data class LimitPostResponse(
	val data: LimitResponse,
)

data class LimitDeleteResponse(
	val data: LimitResponse,
)

data class LimitPutResponse(
	val data: LimitResponse,
)

data class LimitGetResponse(
	val data: ArrayList<LimitResponse>
)

data class LimitResponse(
	@SerializedName("attributes")
	val limit: Limit,
	val id: Int
)
