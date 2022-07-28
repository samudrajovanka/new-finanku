package com.mobprog.finanku.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mobprog.finanku.R
import com.mobprog.finanku.listener.DialogItemListener
import com.mobprog.finanku.preference.AuthPreference
import com.mobprog.finanku.preference.ExpensesPreference
import com.mobprog.finanku.preference.LimitPreference
import com.mobprog.finanku.utils.toCurrencyIDR
import com.mobprog.finanku.view.DialogPromptFragment
import kotlinx.android.synthetic.main.fragment_account.*

class AccountFragment : Fragment(), View.OnClickListener, DialogItemListener {

    private val dialogPrompt by lazy { DialogPromptFragment(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initiateUi()
    }

    override fun onResume() {
        super.onResume()

        val limitPreference = context?.let { LimitPreference(it) }
        tv_monthly_limit_account.text = limitPreference?.getLimit()?.total?.toCurrencyIDR()
    }

    private fun initiateUi() {
        val authPreference = context?.let { AuthPreference(it) }
        val limitPreference = context?.let { LimitPreference(it) }

        tv_name_toolbar_account.text = authPreference?.getUser()?.getFirstName()
        tv_monthly_limit_account.text = limitPreference?.getLimit()?.total?.toCurrencyIDR()

        cl_account_setting.setOnClickListener(this)
        cl_monthly_limit_account.setOnClickListener(this)
        cl_logout_setting.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.cl_account_setting -> {
                context?.startActivity(Intent(context, AccountSettingActivity::class.java))
            }
            R.id.cl_monthly_limit_account -> {
                context?.startActivity(Intent(context, MonthlyLimitActivity::class.java))
            }
            R.id.cl_logout_setting -> {
                dialogPrompt.setMessage("Are you sure want to logout?")
                dialogPrompt.show(requireActivity().supportFragmentManager, "Prompt logout")
            }
        }
    }

    override fun getUserChoice(yes: Boolean) {
        if (yes) {
            val authPreference = context?.let { AuthPreference(it) }
            authPreference?.logout()

            val limitPreference = context?.let { LimitPreference(it) }
            limitPreference?.clearLimit()

            val expensesPreference = context?.let { ExpensesPreference(it) }
            expensesPreference?.reset()

            startActivity(Intent(activity, SignInActivity::class.java))
            activity?.finish()
        }
    }
}