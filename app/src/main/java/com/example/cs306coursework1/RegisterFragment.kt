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
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.compose.material3.Snackbar
import com.example.cs306coursework1.helpers.DB
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment : Fragment() {

    // Auth variables
    private var auth = Firebase.auth

    lateinit var usernameText: TextInputEditText
    lateinit var emailText: TextInputEditText
    lateinit var passwordText: TextInputEditText
    lateinit var accountTypeSelect: AutoCompleteTextView
    lateinit var registerButton: Button

    lateinit var mainActivityIntent: Intent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivityIntent = Intent(activity, MainActivity::class.java)

        usernameText = view.findViewById(R.id.usernameInput)
        emailText = view.findViewById(R.id.emailInput)
        passwordText = view.findViewById(R.id.passwordInput)
        accountTypeSelect = view.findViewById(R.id.accountTypeSelect)
        registerButton = view.findViewById(R.id.registerButton)

        // Set the first account type ("Standard") in the string array as the default
        accountTypeSelect.setText(resources.getStringArray(R.array.account_types)[0])

        val registerBackButton = view.findViewById<Button>(R.id.registerBackButton)

        // Make the back button on the top left corner
        // function the same as pressing the system back button
        registerBackButton?.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        registerButton.setOnClickListener {
            createAccount(view)
        }

    }

    private fun createAccount(view: View) {
        auth.createUserWithEmailAndPassword(
            emailText.text.toString(),
            passwordText.text.toString()
        ).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                DB.createNewUser(
                    task.result.user?.uid.toString(),
                    usernameText.text.toString(),
                    AccountType.getTypeFromString(accountTypeSelect.text.toString())
                ).addOnSuccessListener {
                    mainActivityIntent.putExtra(
                        "user_details",
                        UserDetails(
                            task.result.user?.uid.toString(),
                            usernameText.text.toString(),
                            emailText.text.toString(),
                            AccountType.getTypeFromString(accountTypeSelect.text.toString())
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