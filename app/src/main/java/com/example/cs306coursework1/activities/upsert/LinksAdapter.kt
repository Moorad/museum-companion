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

class LinksAdapter(
    private val modelArrayList: ArrayList<LinksModel>,
) :
    RecyclerView.Adapter<LinksAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var linkText = itemView.findViewById<View>(R.id.textEditText) as TextView
        var linkURL = itemView.findViewById<View>(R.id.urlEditText) as TextView
        var deleteButton = itemView.findViewById<View>(R.id.linkDeleteButton) as Button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.links_rv_row, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return modelArrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = modelArrayList[position]

        holder.linkText.addTextChangedListener(object : TextWatcher {
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
                    val input = holder.linkText.text.toString()

                    info.setLinkText(input)
                }

                runnable?.let {
                    handler.postDelayed(it, 500)
                }
            }
        })

        holder.linkURL.addTextChangedListener(object : TextWatcher {
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
                    val input = holder.linkURL.text.toString()

                    info.setLinkURL(input)
                }

                runnable?.let {
                    handler.postDelayed(it, 500)
                }
            }
        })

        holder.deleteButton.setOnClickListener {
            modelArrayList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun addItem(model: LinksModel) {
        modelArrayList.add(model)
        notifyItemInserted(itemCount)
    }

    fun getModels(): ArrayList<LinksModel> {
        return modelArrayList
    }
}