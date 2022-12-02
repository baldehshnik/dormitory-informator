package com.firstapplication.dormapp.ui.fragments

import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

open class BasicFragment : Fragment() {
    fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun getStringFromRes(@StringRes id: Int): String {
        return resources.getString(id)
    }

    fun turnOnBottomNavView(@IdRes id: Int) {
        requireActivity().findViewById<BottomNavigationView>(id)?.isVisible = true
    }
}