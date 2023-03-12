package com.firstapplication.dormapp.ui.fragments.login

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.firstapplication.dormapp.ADMIN_KEY
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.FragmentAdminLoginBinding
import com.firstapplication.dormapp.ui.activity.LOGIN_KEY
import com.firstapplication.dormapp.ui.activity.LOGIN_USER_PREF
import com.firstapplication.dormapp.ui.fragments.login.MainLoginFragment.Companion.ADMIN_INIT
import com.firstapplication.dormapp.ui.fragments.login.MainLoginFragment.Companion.ADMIN_LOGIN_KEY

class AdminLoginDialogFragment : DialogFragment() {

    private lateinit var editTextKey: EditText

    private val positiveListener = DialogInterface.OnClickListener { dialogInterface, _ ->
        if (editTextKey.text.toString() == ADMIN_KEY) {
            val sharedPreferences = requireActivity().getSharedPreferences(LOGIN_USER_PREF, MODE_PRIVATE)
            sharedPreferences.edit()
                .putString(LOGIN_KEY, resources.getString(R.string.admin_key))
                .apply()

            Log.i(this.javaClass.simpleName, resources.getString(R.string.user_saved))
            parentFragmentManager.setFragmentResult(ADMIN_LOGIN_KEY, bundleOf(ADMIN_INIT to true))
        } else {
            Toast.makeText(requireContext(), resources.getString(R.string.key_not_valid), Toast.LENGTH_SHORT).show()
            Log.i(this.javaClass.simpleName, resources.getString(R.string.key_not_valid))
            parentFragmentManager.setFragmentResult(ADMIN_LOGIN_KEY, bundleOf(ADMIN_INIT to false))
        }
        dialogInterface.dismiss()
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = FragmentAdminLoginBinding.inflate(layoutInflater, null, false)
        editTextKey = binding.etKey

        return AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setIcon(R.drawable.ic_key)
            .setView(binding.root)
            .setTitle(resources.getString(R.string.admin_dialog_title))
            .setPositiveButton(resources.getString(R.string.confirm), positiveListener)
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .create()
    }

    companion object {
        @JvmStatic
        val TAG = "ADMIN_LOGIN_TAG"

        @JvmStatic
        fun newInstance(): AdminLoginDialogFragment {
            return AdminLoginDialogFragment()
        }
    }
}