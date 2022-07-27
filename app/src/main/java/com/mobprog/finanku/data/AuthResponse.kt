package com.mobprog.finanku.data

data class AuthResponse(
	val jwt: String,
	val user: User
)

data class UpdateUserResponse(
	val user: User
)

data class DeleteUserResponse(
	val user: User
)

