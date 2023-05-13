package com.example.cs306coursework1.activities.upsert

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.R
import com.example.cs306coursework1.helpers.Misc
import com.example.cs306coursework1.helpers.Storage

class GalleryAdapter(
    private val modelArrayList: ArrayList<GalleryModel>,
) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    private var removeListener: (position: Int) -> Unit = { }
    private var isDisabled = false

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image = itemView.findViewById<View>(R.id.image) as ImageView
        var imageName = itemView.findViewById<View>(R.id.imageName) as TextView
        var deleteButton = itemView.findViewById<View>(R.id.imageDelete) as Button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.gallery_rv_row, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return modelArrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = modelArrayList[position]

        if (isDisabled) {
            holder.deleteButton.visibility = View.GONE
        }

        Misc.setImageFromURL(info.getImageURL(), holder.image)

        holder.imageName.text = info.getImageName()

        holder.deleteButton.setOnClickListener {
            Storage.deleteImage(info.getImageURL())
            modelArrayList.removeAt(holder.adapterPosition)
            removeListener(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)

        }
    }

    fun addItem(model: GalleryModel) {
        modelArrayList.add(model)
        notifyItemInserted(itemCount)
    }

    fun setOnRemoveListener(removeListener: (position: Int) -> Unit) {
        this.removeListener = removeListener
    }

    fun disableAllViews() {
        isDisabled = true
    }
}