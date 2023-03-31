package com.example.cs306coursework1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import com.example.cs306coursework1.helpers.DB
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

    // Auth variables
    private var auth = Firebase.auth

    lateinit var emailText: TextInputEditText
    lateinit var passwordText: TextInputEditText
    lateinit var loginButton: Button

    lateinit var mainActivityIntent: Intent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivityIntent = Intent(activity, MainActivity::class.java)

        emailText = view.findViewById(R.id.emailInput)
        passwordText = view.findViewById(R.id.passwordInput)
        loginButton = view.findViewById(R.id.loginButton)

        val loginBackButton = view.findViewById<Button>(R.id.loginBackButton)

        // Make the back button on the top left corner
        // function the same as pressing the system back button
        loginBackButton?.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        loginButton.setOnClickListener {
            signInUser(view)
        }
    }

    private fun signInUser(view: View) {
        auth.signInWithEmailAndPassword(
            emailText.text.toString(),
            passwordText.text.toString()
        ).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                Log.println(Log.INFO, "current_user", task.result.user.toString())

                DB.getUserByUID(task.result.user?.uid.toString())
                    .addOnSuccessListener { documents ->

                        val doc = documents.first()

                        mainActivityIntent.putExtra(
                            "user_details",
                            UserDetails(
                                doc.data["uid"].toString(),
                                doc.data["name"].toString(),
                                emailText.text.toString(),
                                AccountType.getTypeFromString(doc.data["type"].toString())
                            )
                        )

                        startActivity(mainActivityIntent)
                    }
                    .addOnFailureListener { exception ->
                        displayError(view, exception.message.toString())
                    }
            } else {
                displayError(view, task.exception?.message.toString())
            }
        }
    }

    private fun displayError(view: View, message: String) {
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