package com.example.cs306coursework1.activities.upsert

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.R
import com.google.android.material.textfield.TextInputLayout

class TagAdapter(
    private val tagArrayList: ArrayList<String>,
) :
    RecyclerView.Adapter<TagAdapter.ViewHolder>() {
    private var isDisabled = false;

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tagLayout = itemView.findViewById<View>(R.id.tagInputLayout) as TextInputLayout
        var tagText = itemView.findViewById<View>(R.id.tagEditText) as TextView
        var deleteButton = itemView.findViewById<View>(R.id.tagDeleteButton) as Button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.tags_rv_row, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return tagArrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tagText.text = tagArrayList[position];

        if (isDisabled) {

            holder.tagLayout.isEnabled = false
            holder.deleteButton.visibility = View.GONE
        }

        holder.tagText.addTextChangedListener(object : TextWatcher {
            private val handler = Handler()
            private var runnable: Runnable? = null

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Nothing
            }

            override fun afterTextChanged(p0: Editable?) {
                runnable?.let { handler.removeCallbacks(it) }

                runnable = Runnable {
                    val input = holder.tagText.text.toString()

                    tagArrayList[holder.adapterPosition] = input
                }

                runnable?.let {
                    handler.postDelayed(it, 500)
                }
            }
        })

        holder.deleteButton.setOnClickListener {
            tagArrayList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun addItem(tag: String) {
        tagArrayList.add(tag)
        notifyItemInserted(itemCount)
    }

    fun getTags(): ArrayList<String> {
        return tagArrayList
    }

    fun disableAllViews() {
        isDisabled = true
    }
}