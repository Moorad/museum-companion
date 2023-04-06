package com.example.cs306coursework1.information

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.R
import com.example.cs306coursework1.helpers.Misc

class LinksAdapter(private val context: Context, private val linkArrayList: ArrayList<LinksModal>) :
    RecyclerView.Adapter<LinksAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView = itemView.findViewById<View>(R.id.linkText) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinksAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.links_layout, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return linkArrayList.size
    }

    override fun onBindViewHolder(holder: LinksAdapter.ViewHolder, position: Int) {
        val info = linkArrayList[position]

        holder.textView.text = info.getLinkTitle()

        holder.textView.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(info.getLinkURL())
            )

            context.startActivity(browserIntent)
        }
    }
}