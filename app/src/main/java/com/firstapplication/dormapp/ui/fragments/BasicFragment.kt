package com.firstapplication.dormapp.ui.fragments

import android.view.View
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.bottomnavigation.BottomNavigationView

open class BasicFragment : Fragment() {
    fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun snackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    fun getStringFromRes(@StringRes id: Int): String {
        return resources.getString(id)
    }

    fun switchBottomNavViewVisibility(@IdRes id: Int, value: Int) {
        when (value) {
            VISIBLE -> {
                requireActivity().findViewById<BottomNavigationView>(id)?.isVisible = true
            }
            INVISIBLE -> {
                requireActivity().findViewById<BottomNavigationView>(id)?.isInvisible = true
            }
            GONE -> {
                requireActivity().findViewById<BottomNavigationView>(id)?.isVisible = false
            }
        }
    }

    companion object {
        const val VISIBLE = 1
        const val INVISIBLE = 2
        const val GONE = 3
    }
}