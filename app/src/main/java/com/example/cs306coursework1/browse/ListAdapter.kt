package com.example.cs306coursework1.browse

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.information.InformationActivity
import com.example.cs306coursework1.R
import com.google.android.material.chip.Chip

class ListAdapter(
    private val context: Context,
    private val cardArrayList: ArrayList<ListModal>
) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private val infoActivityIntent = Intent(context, InformationActivity::class.java)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameView = itemView.findViewById<View>(R.id.cardName) as TextView
        var descriptionView = itemView.findViewById<View>(R.id.cardDescription) as TextView
        var createdAtView = itemView.findViewById<View>(R.id.cardAdded) as TextView
        var labelView = itemView.findViewById<View>(R.id.cardLabel) as TextView
        var tagContainer = itemView.findViewById<View>(R.id.tagContainer) as LinearLayout
        var visitButton = itemView.findViewById<View>(R.id.cardVisitButton) as Button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.artefact_card_layout, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return cardArrayList.size
    }

    override fun onBindViewHolder(holder: ListAdapter.ViewHolder, position: Int) {
        val info = cardArrayList[position]

        holder.nameView.text = info.getName()
        holder.descriptionView.text = info.getDescription()

        holder.createdAtView.text = info.getCreatedAt()
        holder.labelView.text = "Label: " + info.getLabel()


        info.getTags().forEach { tag ->
            val chip = Chip(context)

            chip.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            val param = chip.layoutParams as ViewGroup.MarginLayoutParams
            param.rightMargin = 10
            chip.layoutParams = param

            chip.text = tag

            holder.tagContainer.addView(chip)
        }

        holder.visitButton.setOnClickListener {

            infoActivityIntent.putExtra("artefact_id", info.getID())
            infoActivityIntent.putExtra("artefact_name", info.getName())
            context.startActivity(infoActivityIntent)
        }
    }
}