package com.example.cs306coursework1.activities.profile

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.marginRight
import androidx.core.view.setMargins
import com.example.cs306coursework1.R
import com.example.cs306coursework1.activities.login.LoginActivity
import com.example.cs306coursework1.data.AccountType
import com.example.cs306coursework1.data.SubmissionType
import com.example.cs306coursework1.data.UserSingleton
import com.example.cs306coursework1.helpers.DB
import com.example.cs306coursework1.helpers.Misc
import com.example.cs306coursework1.helpers.Storage
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import java.time.ZoneId
import kotlin.math.floor

class ProfileFragment(private val AppBar: MaterialToolbar) : Fragment() {

    private lateinit var linearLayout: LinearLayout
    private lateinit var profileImage: CircleImageView
    private lateinit var usernameTextView: TextView
    private lateinit var levelProgressText: TextView
    private lateinit var levelText: TextView
    private lateinit var levelProgressBar: ProgressBar
    private lateinit var guestUserText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var submissionCountText: TextView
    private lateinit var joinedAtText: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        AppBar.title = "Profile"

        linearLayout = view.findViewById(R.id.linearLayout)
        profileImage = view.findViewById(R.id.profileImageView)
        usernameTextView = view.findViewById(R.id.usernameTextView)
        levelProgressText = view.findViewById(R.id.levelProgressTextView)
        levelText = view.findViewById(R.id.levelTextView)
        levelProgressBar = view.findViewById(R.id.levelProgressBar)
        guestUserText = view.findViewById(R.id.guestUserTextView)
        descriptionText = view.findViewById(R.id.descriptionTextView)
        submissionCountText = view.findViewById(R.id.submissionCountTextView)
        joinedAtText = view.findViewById(R.id.joinedAtTextView)

        AppBar.inflateMenu(R.menu.profile_activity_menu)

        AppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_logout -> {
                    Firebase.auth.signOut()

                    val loginActivityIntent = Intent(context, LoginActivity::class.java)
                    startActivity(loginActivityIntent)

                    true
                }
                else -> false
            }
        }

        if (UserSingleton.getAccountType() == AccountType.GUEST) {
            usernameTextView.text = "Guest user"

            profileImage.setImageResource(R.drawable.guest_profile_image)

            guestUserText.visibility = View.VISIBLE
            return view
        }



        DB.getUserByUID(UserSingleton.getID()).addOnSuccessListener { docs ->
            val user = docs.first()
            Misc.setImageFromURL(user["profile_image"].toString(), profileImage)

            usernameTextView.text = Misc.boldText(user["name"].toString())

            val level = (user["level"] as Double)
            val percentageLevel = ((level - floor(level)) * 100).toInt()
            val localDate = (user["created_at"] as Timestamp).toDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate()

            levelProgressText.text = "$percentageLevel%"
            levelText.text = "Level " + floor(level).toInt().toString()

            levelProgressBar.progress = percentageLevel
            descriptionText.text = user["description"].toString()

            joinedAtText.text = localDate.month.toString() + " " + localDate.dayOfMonth.toString()

            DB.getSubmissionsByStatus(SubmissionType.APPROVED, null, UserSingleton.getID())
                .addOnSuccessListener {
                    val submissionCount = it.documents.size.toString()
                    submissionCountText.text = submissionCount
                }
        }.addOnFailureListener { exception ->
            Misc.displaySnackBar(linearLayout, exception.message.toString())
        }

        val singleSelectPhotoMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Storage.uploadImage(uri, "profile_images", linearLayout)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val downloadURL = task.result.toString()

                                val data: HashMap<String, Any?> = hashMapOf(
                                    "profile_image" to downloadURL
                                )

                                DB.updateUser(UserSingleton.getID(), data).addOnSuccessListener {
                                    Misc.setImageFromURL(downloadURL, profileImage)
                                }.addOnFailureListener { exception ->
                                    Misc.displaySnackBar(linearLayout, exception.message.toString())
                                }
                            } else {
                                Misc.displaySnackBar(linearLayout, task.exception.toString())
                            }
                        }
                }
            }

        profileImage.setOnClickListener {
            singleSelectPhotoMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        usernameTextView.setOnClickListener {
            context?.let { ctx ->
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(60, 0, 60, 0)

                val inputLayout =
                    TextInputLayout(ctx)
                inputLayout.layoutParams = lp

                val input = TextInputEditText(inputLayout.context)
                input.inputType = InputType.TYPE_CLASS_TEXT
                input.layoutParams = lp
                input.setText(UserSingleton.getUsername())

                inputLayout.addView(input, lp)

                MaterialAlertDialogBuilder(ctx)
                    .setTitle("Modify username")
                    .setMessage("Set the new username for your profile.")
                    .setView(inputLayout)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Update") { dialogInterface, i ->
                        val newUsername = input.text.toString()
                        val data: HashMap<String, Any?> = hashMapOf(
                            "name" to newUsername
                        )

                        DB.updateUser(UserSingleton.getID(), data).addOnSuccessListener {
                            UserSingleton.setUsername(newUsername)
                            usernameTextView.text = Misc.boldText(newUsername)
                        }.addOnFailureListener { exception ->
                            Misc.displaySnackBar(linearLayout, exception.message.toString())
                        }
                    }
                    .show()
            }
        }

        descriptionText.setOnClickListener {
            context?.let { ctx ->
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(60, 0, 60, 0)

                val inputLayout =
                    TextInputLayout(ctx)
                inputLayout.layoutParams = lp

                val input = TextInputEditText(inputLayout.context)
                input.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
                input.layoutParams = lp
                input.setText(descriptionText.text.toString())

                inputLayout.addView(input, lp)

                MaterialAlertDialogBuilder(ctx)
                    .setTitle("Modify description")
                    .setMessage("Set the new description for your profile.")
                    .setView(inputLayout)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Update") { _, _ ->
                        val newDescription = input.text.toString()
                        val data: HashMap<String, Any?> = hashMapOf(
                            "description" to newDescription
                        )

                        DB.updateUser(UserSingleton.getID(), data).addOnSuccessListener {
                            descriptionText.text = newDescription
                        }.addOnFailureListener { exception ->
                            Misc.displaySnackBar(linearLayout, exception.message.toString())
                        }
                    }
                    .show()
            }
        }

        return view
    }
}