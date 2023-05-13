package com.example.cs306coursework1.activities.points

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.R
import com.example.cs306coursework1.helpers.Misc
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.math.floor

class LeaderboardAdapter(
    private val context: Context,
    private val arrayList: ArrayList<LeaderboardModel>
) :
    RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var container = itemView.findViewById<View>(R.id.container) as LinearLayoutCompat
        var trophy = itemView.findViewById<View>(R.id.trophy) as ImageView
        var positionView = itemView.findViewById<View>(R.id.position) as TextView
        var profileImageView = itemView.findViewById<View>(R.id.icon) as CircleImageView
        var levelProgress = itemView.findViewById<View>(R.id.level_progress) as ProgressBar
        var levelText = itemView.findViewById<View>(R.id.level) as TextView
        var nameView = itemView.findViewById<View>(R.id.name) as TextView
        var points = itemView.findViewById<View>(R.id.points) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.leaderboard_row_layout, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = arrayList[position]

        holder.positionView.text = (position + 1).toString()
        holder.nameView.text = info.getName()
        holder.levelText.text = info.getLevel().toInt().toString()
        holder.levelProgress.progress = ((info.getLevel() - floor(info.getLevel())) * 100).toInt()
        holder.points.text = (info.getLevel() * 100 * 2).toInt().toString() + " pts"

        // Top user
        if (position == 0) {
            holder.trophy.visibility = View.VISIBLE
            holder.positionView.visibility = View.GONE
            holder.trophy.setColorFilter(ContextCompat.getColor(context, R.color.gold))
        } else if (position == 1) {
            holder.trophy.visibility = View.VISIBLE
            holder.positionView.visibility = View.GONE
            holder.trophy.setColorFilter(ContextCompat.getColor(context, R.color.silver))
        } else if (position == 2) {
            holder.trophy.visibility = View.VISIBLE
            holder.positionView.visibility = View.GONE
            holder.trophy.setColorFilter(ContextCompat.getColor(context, R.color.bronze))
        }


        Misc.setImageFromURL(info.getImage(), holder.profileImageView)
    }

}