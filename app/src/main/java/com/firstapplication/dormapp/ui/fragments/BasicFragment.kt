package com.firstapplication.dormapp.ui.fragments

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

open class BasicFragment : Fragment() {

    fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun getStringFromRes(@StringRes id: Int): String {
        return resources.getString(id)
    }
}