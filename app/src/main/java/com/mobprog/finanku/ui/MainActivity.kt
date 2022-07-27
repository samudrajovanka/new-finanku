package com.mobprog.finanku.ui

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mobprog.finanku.R
import com.mobprog.finanku.data.CategoryGetResponse
import com.mobprog.finanku.data.CategoryResponse
import com.mobprog.finanku.data.Data
import com.mobprog.finanku.network.ApiConfig
import com.mobprog.finanku.preference.AuthPreference
import com.mobprog.finanku.view.DialogLoading
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initiateUi()
    }

    private fun initiateUi() {
        val navController = findNavController(R.id.fragment_root)

        bnv_main.setupWithNavController(navController)
    }
}