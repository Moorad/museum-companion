package com.example.cs306coursework1.helpers

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class Misc {
    companion object {

        // Type safe, API version independent function for getting a parcelable from an intent
        fun <T : Parcelable?> getParcelableFromIntent(
            intent: Intent,
            name: String,
            clazz: Class<T>
        ): T? {
            // Get parcelable (fancy object) from activity that we were just in
            val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // This variant of getParcelableExtra requires API level >= 33
                intent.getParcelableExtra(name, clazz)
            } else {
                // If API level is older than Tiramisu (API level 33) use deprecated getParcelableExtra
                @Suppress("DEPRECATION")
                intent.getParcelableExtra<T>(name)
            }
            // They both do the same thing but the old getParcelableExtra was not type safe and the type
            // cannot be inferred so it has to be manually typed. The new getParcelableExtra you have to
            // pass in the class of the type returned making it type safe.

            return data
        }

        fun setImageFromURL(url: String, imageView: ImageView) {
            val imageRef =
                Firebase.storage.getReferenceFromUrl(url)

            imageRef.downloadUrl.addOnSuccessListener { uri ->
                Picasso.get().load(uri.toString()).into(imageView)
            }.addOnFailureListener { exception ->
                displaySnackBar(imageView.rootView, exception.message.toString())
            }
        }

        fun closeKeyboard(view: View) {
            val inputMethodManager =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun displaySnackBar(view: View, message: String) {
            // Display snack bar
            Snackbar.make(
                view,
                message,
                3000
            ).show()
        }
    }
}