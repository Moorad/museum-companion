package com.example.cs306coursework1.activities.browse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.cs306coursework1.R
import com.example.cs306coursework1.helpers.DB
import com.example.cs306coursework1.helpers.Misc
import com.example.cs306coursework1.activities.information.InformationActivity
import com.example.cs306coursework1.data.UserSingleton
import com.google.android.material.textfield.TextInputEditText

class MapFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        val museumNameView = view.findViewById<TextView>(R.id.museumName)
        museumNameView.text = UserSingleton.getSelectedMuseumName().toString()

        val artefactNumInput = view.findViewById<TextInputEditText>(R.id.artefactNumInput)

        val infoActivityIntent = Intent(this.context, InformationActivity::class.java)

        artefactNumInput.setOnEditorActionListener { textView, id, keyEvent ->
            Log.println(Log.INFO, "input text", artefactNumInput.text.toString())

            if (id == EditorInfo.IME_ACTION_DONE) {
                Misc.closeKeyboard(view)

                DB.getArtefactByProperty(
                    UserSingleton.getSelectedMuseumID().toString(),
                    "label",
                    artefactNumInput.text.toString()
                ).addOnSuccessListener { documents ->
                    if (documents.size() == 0) {
                        artefactNumInput.error =
                            "No artefact found that corresponds to this label in this museum"
                        return@addOnSuccessListener
                    }

                    val doc = documents.first()

                    infoActivityIntent.putExtra("artefact_id", doc.id)
                    startActivity(infoActivityIntent)
                }.addOnFailureListener { exception ->
                    Misc.displaySnackBar(view, exception.message.toString())
                }
                true
            } else {
                false
            }

        }

        return view
    }
}