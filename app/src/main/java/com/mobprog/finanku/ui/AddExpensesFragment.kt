package com.mobprog.finanku.ui

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.mobprog.finanku.R
import com.mobprog.finanku.data.*
import com.mobprog.finanku.network.ApiConfig
import com.mobprog.finanku.preference.AuthPreference
import com.mobprog.finanku.preference.ExpensesPreference
import com.mobprog.finanku.utils.onChangeRemoveError
import com.mobprog.finanku.view.DialogLoading
import kotlinx.android.synthetic.main.fragment_add_expenses.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime

class AddExpensesFragment : Fragment(), View.OnClickListener {
    private val dialogLoading by lazy { DialogLoading() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_expenses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDropdownMenu(Data.categories)
        initiateUi()
    }

    private fun initiateUi() {
        menu_dropdown_type_expense_money.addTextChangedListener(
            onChangeRemoveError(
                til_set_category_type_expense_money
            )
        )
        et_set_description_type_expense_money.addTextChangedListener(
            onChangeRemoveError(
                til_set_description_type_expense_money
            )
        )
        et_set_amount_money_expense_money.addTextChangedListener(
            onChangeRemoveError(
                til_set_amount_money_expense_money
            )
        )

        btn_input_expense_money.setOnClickListener(this)
    }

    private fun setDropdownMenu(categories: ArrayList<CategoryResponse>) {
        val items = ArrayList<String>()
        for (category in categories) {
            items.add(category.category?.type.toString())
        }

        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, items)
        menu_dropdown_type_expense_money.setAdapter(adapter)
    }

    private fun validateInput(): Boolean {
        val category = menu_dropdown_type_expense_money.text.toString()
        val description = et_set_description_type_expense_money.text.toString()
        val expensesMoney = et_set_amount_money_expense_money.text.toString()
        var isValid = true

        if (TextUtils.isEmpty(category)) {
            til_set_category_type_expense_money.error = "Category is required"
            isValid = false
        }

        if (TextUtils.isEmpty(description)) {
            til_set_description_type_expense_money.error = "Description is required"
            isValid = false
        }

        if (TextUtils.isEmpty(expensesMoney)) {
            til_set_amount_money_expense_money.error = "Expenses money is required"
            isValid = false
        }

        return isValid
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_input_expense_money -> {
                if (validateInput()) {
                    val category = Data.getCategoryByName(menu_dropdown_type_expense_money.text.toString())
                    val description = et_set_description_type_expense_money.text.toString()
                    val expensesMoney = et_set_amount_money_expense_money.text.toString().toInt()

                    addExpenses(category, description, expensesMoney)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addExpenses(
        category: CategoryResponse,
        description: String,
        amount: Int,
    ) {
        activity?.supportFragmentManager?.let { dialogLoading.show(it, "loading") }

        val authPreference = AuthPreference(requireActivity())

        val client = ApiConfig.getApiService().addExpenses(
            authPreference.getToken(),
            category.id,
            description,
            amount,
            LocalDateTime.now(),
            authPreference.getUser().id
        )
        client.enqueue(object : Callback<ExpensesPostResponse> {
            override fun onResponse(
                call: Call<ExpensesPostResponse>,
                response: Response<ExpensesPostResponse>
            ) {
                dialogLoading.stop()

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val expensesPreference = context?.let { ExpensesPreference(it) }
                        when (category.category?.type) {
                            "Food" -> expensesPreference?.addFood(amount)
                            "Shop" -> expensesPreference?.addShop(amount)
                            "Travel" -> expensesPreference?.addTravel(amount)
                            "Others" -> expensesPreference?.addOthers(amount)
                        }

                        Toast.makeText(activity, "Successfully add expense money", Toast.LENGTH_SHORT)
                            .show()
                        findNavController().navigate(R.id.navigation_home)
                    }
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()?.string())
                        Toast.makeText(
                            activity,
                            jObjError.getJSONObject("error").getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                    }
                    Log.e(ContentValues.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ExpensesPostResponse>, t: Throwable) {
                dialogLoading.stop()

                Toast.makeText(
                    activity,
                    "Error. Please contact developer",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }

        })
    }
}