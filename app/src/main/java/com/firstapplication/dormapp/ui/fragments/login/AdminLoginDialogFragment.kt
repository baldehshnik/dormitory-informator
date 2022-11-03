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
import androidx.fragment.app.DialogFragment
import com.firstapplication.dormapp.ADMIN_KEY
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.ui.activity.LoginActivity

class AdminLoginDialogFragment : DialogFragment() {

    private lateinit var editTextKey: EditText

    private val positiveListener = DialogInterface.OnClickListener { dialogInterface, _ ->
        if (editTextKey.text.toString() == ADMIN_KEY) {
            val sharedPreferences = requireActivity().getSharedPreferences(LoginActivity.LOGIN_USER_PREF, MODE_PRIVATE)
            sharedPreferences.edit()
                .putString(LoginActivity.LOGIN_KEY, resources.getString(R.string.admin_key))
                .apply()

            Log.i(this.javaClass.simpleName, resources.getString(R.string.user_saved))
        } else {
            Toast.makeText(requireContext(), resources.getString(R.string.key_not_valid), Toast.LENGTH_SHORT).show()
            Log.i(this.javaClass.simpleName, resources.getString(R.string.key_not_valid))
        }
        dialogInterface.dismiss()
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.fragment_admin_login, null)
        editTextKey = view.findViewById(R.id.etKey)

        return AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setIcon(R.drawable.ic_key)
            .setView(view)
            .setTitle(resources.getString(R.string.admin_dialog_title))
            .setPositiveButton(resources.getString(R.string.confirm), positiveListener)
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .create()
    }

    companion object {
        const val TAG = "ADMIN LOGIN TAG"
    }
}