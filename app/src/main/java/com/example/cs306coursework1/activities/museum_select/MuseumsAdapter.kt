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
import com.example.cs306coursework1.activities.browse.BrowseActivity
import com.example.cs306coursework1.data.AccountType
import com.example.cs306coursework1.data.MuseumDetails
import com.example.cs306coursework1.data.UserSingleton

class MuseumsAdapter(
    private val context: Context,
    private val cardArrayList: ArrayList<MuseumsModal>
) :
    RecyclerView.Adapter<MuseumsAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameView = itemView.findViewById<View>(R.id.cardName) as TextView
        var curatorView = itemView.findViewById<View>(R.id.cardCurator) as TextView
        var descriptionView = itemView.findViewById<View>(R.id.cardDescription) as TextView
        var visitButtonView = itemView.findViewById<View>(R.id.cardVisitButton) as Button
        var editButtonView = itemView.findViewById<View>(R.id.cardEditButton) as Button
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
        holder.curatorView.text = "Curator: " + info.getCuratorName()
        holder.descriptionView.text = info.getDescription()

        // If the user is curator, show the edit buttons
        if (UserSingleton.getAccountType() == AccountType.CURATOR) {
            /* TODO: check whether the museum belongs to the user
                if it does then show the edit button, otherwise keep
                it hidden */
            holder.editButtonView.visibility = View.VISIBLE
        }

        holder.visitButtonView.setOnClickListener {
            val browseActivityIntent = Intent(context, BrowseActivity::class.java)

            UserSingleton.setSelectedMuseumID(info.getID())
            UserSingleton.setSelectedMuseumName(info.getName())
            context.startActivity(browseActivityIntent)
        }
    }

}