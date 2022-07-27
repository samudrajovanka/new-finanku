package com.mobprog.finanku.network

import com.mobprog.finanku.data.*
import retrofit2.Call
import retrofit2.http.*
import java.time.LocalDate
import java.time.LocalDateTime

interface ApiService {
    @FormUrlEncoded
    @POST("auth/local")
    fun login(
        @Field("identifier") identifier: String,
        @Field("password") password: String
    ): Call<AuthResponse>

    @FormUrlEncoded
    @POST("auth/local/register")
    fun register(
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<AuthResponse>

    @FormUrlEncoded
    @POST("limits")
    fun setLimits(
        @Header("Authorization") authorization: String?,
        @Field("data.food") food: Int,
        @Field("data.shop") shop: Int,
        @Field("data.travel") travel: Int,
        @Field("data.others") others: Int,
        @Field("data.total") total: Int,
        @Field("data.user") userId: Int
    ): Call<LimitPostResponse>

    @FormUrlEncoded
    @PUT("limits/{id}")
    fun updateLimits(
        @Header("Authorization") authorization: String?,
        @Path("id") limitId: Int,
        @Field("data.food") food: Int,
        @Field("data.shop") shop: Int,
        @Field("data.travel") travel: Int,
        @Field("data.others") others: Int,
        @Field("data.total") total: Int,
    ): Call<LimitPutResponse>

    @DELETE("limits/{id}")
    fun deleteLimit(
        @Header("Authorization") authorization: String?,
        @Path("id") limitId: Int,
    ): Call<LimitDeleteResponse>

    @GET("limits")
    fun getLimitsByUserId(
        @Header("Authorization") authorization: String?,
        @Query("filters[user][id][\$eq]") userId: String
    ): Call<LimitGetResponse>

    @FormUrlEncoded
    @PUT("users/{id}")
    fun updateEmail(
        @Header("Authorization") authorization: String?,
        @Path("id") userId: String,
        @Field("email") email: String,
        @Field("username") username: String,
    ): Call<UpdateUserResponse>

    @FormUrlEncoded
    @PUT("users/{id}")
    fun updatePassword(
        @Header("Authorization") authorization: String?,
        @Path("id") userId: String,
        @Field("password") password: String,
    ): Call<UpdateUserResponse>

    @DELETE("users/{id}")
    fun deleteUser(
        @Header("Authorization") authorization: String?,
        @Path("id") userId: String,
    ): Call<DeleteUserResponse>

    @FormUrlEncoded
    @POST("expenses")
    fun addExpenses(
        @Header("Authorization") authorization: String?,
        @Field("data.type") type: Int?,
        @Field("data.description") description: String,
        @Field("data.amount") amount: Int,
        @Field("data.date") date: LocalDateTime,
        @Field("data.user") user: Int,
    ): Call<ExpensesPostResponse>

    @GET("categories")
    fun getCategories(): Call<CategoryGetResponse>

    @GET("expenses?populate=*&sort=date%3Adesc")
    fun getExpensesByUserId(
        @Header("Authorization") authorization: String?,
        @Query("filters[user][id][\$eq]") userId: String
    ): Call<ExpensesGetResponse>
}