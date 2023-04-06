package com.example.cs306coursework1.information

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.R
import com.example.cs306coursework1.helpers.Misc
import com.squareup.picasso.Picasso


class GalleryAdapter(private val imageArrayList: ArrayList<String>) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView = itemView.findViewById<View>(R.id.galleryImage) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.gallery_layout, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return imageArrayList.size
    }

    override fun onBindViewHolder(holder: GalleryAdapter.ViewHolder, position: Int) {
        val url = imageArrayList[position]

        Misc.setImageFromURL(url, holder.imageView)

    }
}