package com.example.cs306coursework1.activities.museum_select

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.*
import com.example.cs306coursework1.activities.main.MainActivity
import com.example.cs306coursework1.activities.museum_upsert.MuseumUpsertActivity
import com.example.cs306coursework1.data.AccountType
import com.example.cs306coursework1.data.UpsertMode
import com.example.cs306coursework1.data.UserSingleton
import com.example.cs306coursework1.helpers.DB
import com.example.cs306coursework1.helpers.Misc
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MuseumsAdapter(
    private val context: Context,
    private val cardArrayList: ArrayList<MuseumsModel>,
    private val constraintLayout: View
) :
    RecyclerView.Adapter<MuseumsAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameView = itemView.findViewById<View>(R.id.cardName) as TextView
        var curatorView = itemView.findViewById<View>(R.id.cardCurator) as TextView
        var descriptionView = itemView.findViewById<View>(R.id.cardDescription) as TextView
        var visitButtonView = itemView.findViewById<View>(R.id.cardVisitButton) as Button
        var editButtonView = itemView.findViewById<View>(R.id.cardEditButton) as Button
        var deleteButtonView = itemView.findViewById<View>(R.id.cardDeleteButton) as Button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.museum_card_layout, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return cardArrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = cardArrayList[position]

        holder.nameView.text = info.getName()
        holder.curatorView.text = "Owner: " + info.getCuratorName()
        holder.descriptionView.text = info.getDescription()

        // If the user is curator, show the edit buttons
        if (UserSingleton.getAccountType() == AccountType.CURATOR) {
            holder.editButtonView.visibility = View.VISIBLE
            holder.deleteButtonView.visibility = View.VISIBLE
        }

        holder.visitButtonView.setOnClickListener {
            val mainActivityIntent = Intent(context, MainActivity::class.java)

            UserSingleton.setSelectedMuseumID(info.getID())
            UserSingleton.setSelectedMuseumName(info.getName())
            context.startActivity(mainActivityIntent)
        }

        holder.editButtonView.setOnClickListener {
            val mainActivityIntent = Intent(context, MuseumUpsertActivity::class.java)
            mainActivityIntent.putExtra("mode", UpsertMode.UPDATE)
            mainActivityIntent.putExtra("museum_id", info.getID())
            context.startActivity(mainActivityIntent)
        }

        holder.deleteButtonView.setOnClickListener {
            MaterialAlertDialogBuilder(
                context,
                com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
            )
                .setTitle("Permanently delete?")
                .setIcon(R.drawable.ic_delete)
                .setMessage("Are you sure you want to delete this item? You will not be able to restore this after deleting.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete") { _, _ ->
                    DB.deleteMuseum(info.getID()).addOnSuccessListener {
                        cardArrayList.removeAt(holder.adapterPosition)
                        notifyItemRemoved(holder.adapterPosition)

                        Misc.displaySnackBar(constraintLayout, "Deleted successfully!")
                    }
                }
                .show()


        }
    }

}