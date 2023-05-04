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
import com.example.cs306coursework1.R
import com.example.cs306coursework1.activities.upsert.UpsertActivity
import com.example.cs306coursework1.data.UpsertMode
import kotlin.math.floor

class SubmissionAdapter(
    private val imageModeArrayList: ArrayList<SubmissionModal>,
    val context: Context
) :
    RecyclerView.Adapter<SubmissionAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgView = itemView.findViewById<View>(R.id.icon) as ImageView
        var txtView = itemView.findViewById<View>(R.id.name) as TextView
        var levelTxtView = itemView.findViewById<View>(R.id.level) as TextView
        var levelProgressBar = itemView.findViewById<View>(R.id.level_progress) as ProgressBar
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

        val levelInt = info.getLevel().toInt()
        val levelProgress = ((info.getLevel() - floor(info.getLevel())) * 100).toInt()

        holder.levelTxtView.text = levelInt.toString()
        holder.levelProgressBar.progress = levelProgress

        holder.editButton.setOnClickListener {
            val upsertActivityIntent = Intent(context, UpsertActivity::class.java)
            upsertActivityIntent.putExtra("mode", UpsertMode.UPDATE)
            upsertActivityIntent.putExtra("artefact_id", info.getArtefactID())
            context.startActivity(upsertActivityIntent)
        }

    }
}