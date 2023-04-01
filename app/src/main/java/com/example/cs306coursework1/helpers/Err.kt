package com.example.cs306coursework1.helpers

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar

class Err {
    companion object {
        // Closes keyboard and displays a snack bar
        fun displaySnackBar(view: View, message: String) {
            // Close keyboard
            val inputMethodManager =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

            // Display snack bar
            Snackbar.make(
                view,
                message,
                5000
            ).show()
        }

    }
}