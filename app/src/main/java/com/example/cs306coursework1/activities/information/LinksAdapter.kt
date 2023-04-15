package com.example.cs306coursework1.activities.information

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.R

class LinksAdapter(private val context: Context, private val linkArrayList: ArrayList<LinksModel>) :
    RecyclerView.Adapter<LinksAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView = itemView.findViewById<View>(R.id.linkText) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.links_layout, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return linkArrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = linkArrayList[position]

        holder.textView.text =
            Html.fromHtml("<u>" + info.getLinkTitle() + "</u>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        holder.textView.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(info.getLinkURL())
            )

            context.startActivity(browserIntent)
        }
    }
}