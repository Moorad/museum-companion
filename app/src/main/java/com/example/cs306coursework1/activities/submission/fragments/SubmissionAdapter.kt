package com.example.cs306coursework1.activities.submission.fragments

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.cs306coursework1.R
import com.example.cs306coursework1.activities.upsert.UpsertActivity
import com.example.cs306coursework1.data.UpsertMode
import com.example.cs306coursework1.helpers.DB
import com.example.cs306coursework1.helpers.Misc
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.math.floor

class SubmissionAdapter(
    private val imageModeArrayList: ArrayList<SubmissionModal>,
    private val context: Context,
    private val constraintLayout: View
) :
    RecyclerView.Adapter<SubmissionAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgView = itemView.findViewById<View>(R.id.icon) as ImageView
        var txtView = itemView.findViewById<View>(R.id.name) as TextView
        var lastUpdatedView = itemView.findViewById<View>(R.id.lastUpdated) as TextView
        var levelTxtView = itemView.findViewById<View>(R.id.level) as TextView
        var levelProgressBar = itemView.findViewById<View>(R.id.level_progress) as ProgressBar
        var approveButton = itemView.findViewById<View>(R.id.approveButton) as Button
        var denyButton = itemView.findViewById<View>(R.id.denyButton) as Button
        var editButton = itemView.findViewById<View>(R.id.editButton) as Button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.row_layout, parent, false)

        return ViewHolder(v)

    }

    override fun getItemCount(): Int {
        return imageModeArrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = imageModeArrayList[position]

        holder.imgView.setImageResource(info.getImage())
        holder.txtView.text = info.getName()
        holder.lastUpdatedView.text = info.getLastUpdated()

        val levelInt = info.getLevel().toInt()
        val levelProgress = ((info.getLevel() - floor(info.getLevel())) * 100).toInt()

        holder.levelTxtView.text = levelInt.toString()
        holder.levelProgressBar.progress = levelProgress

        holder.approveButton.setOnClickListener {
            MaterialAlertDialogBuilder(
                context,
                com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
            )
                .setTitle("You are about to approve this artefact")
                .setIcon(R.drawable.ic_approve)
                .setMessage("Are you sure you want to approve this artefact? You can update and delete this item later.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Approve") { _, _ ->
                    val submissionData: HashMap<String, Any?> = hashMapOf(
                        "artefact_id" to info.getArtefactID(),
                        "status" to "approved"
                    )

                    val artefactData: HashMap<String, Any?> = hashMapOf(
                        "isApproved" to true
                    )

                    DB.updateSubmissions(submissionData).addOnSuccessListener {
                        DB.updateBasicArtefact(info.getArtefactID(), artefactData)
                            .addOnSuccessListener {
                                Misc.displaySnackBar(constraintLayout, "Approved successfully!")
                            }.addOnFailureListener { exception ->
                                Misc.displaySnackBar(constraintLayout, exception.message.toString())
                            }
                    }.addOnFailureListener { exception ->
                        Misc.displaySnackBar(constraintLayout, exception.message.toString())
                    }
                }
                .show()
        }

        holder.denyButton.setOnClickListener {
            MaterialAlertDialogBuilder(
                context,
                com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
            )
                .setTitle("You are about to deny this artefact")
                .setIcon(R.drawable.ic_denied)
                .setMessage("Are you sure you want to deny this artefact? This can be approved later.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Deny") { _, _ ->
                    val submissionData: HashMap<String, Any?> = hashMapOf(
                        "artefact_id" to info.getArtefactID(),
                        "status" to "denied"
                    )

                    DB.updateSubmissions(submissionData).addOnSuccessListener {
                        Misc.displaySnackBar(constraintLayout, "Denied successfully!")
                    }.addOnFailureListener { exception ->
                        Misc.displaySnackBar(constraintLayout, exception.message.toString())
                    }
                }
                .show()
        }

        holder.editButton.setOnClickListener {
            val upsertActivityIntent = Intent(context, UpsertActivity::class.java)
            upsertActivityIntent.putExtra("mode", UpsertMode.UPDATE)
            upsertActivityIntent.putExtra("artefact_id", info.getArtefactID())
            context.startActivity(upsertActivityIntent)
        }

    }
}