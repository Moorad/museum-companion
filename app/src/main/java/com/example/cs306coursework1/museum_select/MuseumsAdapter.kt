package com.example.cs306coursework1.museum_select

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.R

class MuseumsAdapter(private val cardArrayList: ArrayList<MuseumsModal>) :
    RecyclerView.Adapter<MuseumsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameView = itemView.findViewById<View>(R.id.cardName) as TextView
        var curatorView = itemView.findViewById<View>(R.id.cardCurator) as TextView
        var descriptionView = itemView.findViewById<View>(R.id.cardDescription) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MuseumsAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.museum_card_layout, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return cardArrayList.size
    }

    override fun onBindViewHolder(holder: MuseumsAdapter.ViewHolder, position: Int) {
        val info = cardArrayList[position]

        holder.nameView.text = info.getName()
        holder.curatorView.text = "Curator: " + info.getCuratorName()
        holder.descriptionView.text = info.getDescription()
    }

}