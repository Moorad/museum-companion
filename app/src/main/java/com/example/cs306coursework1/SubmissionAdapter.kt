package com.example.cs306coursework1

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import kotlin.math.floor

class SubmissionAdapter(private val imageModeArrayList: ArrayList<SubmissionModal>) : RecyclerView.Adapter<SubmissionAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View ): RecyclerView.ViewHolder(itemView) {
        var imgView = itemView.findViewById<View>(R.id.icon) as ImageView
        var txtView = itemView.findViewById<View>(R.id.name) as TextView
        var levelTxtView = itemView.findViewById<View>(R.id.level) as TextView
        var levelProgressBar = itemView.findViewById<ProgressBar>(R.id.level_progress)
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

        val levelInt = info.getLevel().toInt()
        val levelProgress = ((info.getLevel() - floor(info.getLevel())) * 100).toInt()

        holder.levelTxtView.text = levelInt.toString()
        holder.levelProgressBar.progress = levelProgress
    }
}