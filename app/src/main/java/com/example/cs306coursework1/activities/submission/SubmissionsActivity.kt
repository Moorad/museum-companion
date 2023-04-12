package com.example.cs306coursework1.activities.submission

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cs306coursework1.R
import com.example.cs306coursework1.activities.upsert.UpsertActivity
import com.google.android.material.appbar.MaterialToolbar

class SubmissionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submissions)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_add -> {
                    startActivity(Intent(this, UpsertActivity::class.java))
                    true
                }
                else -> false
            }
        }

        val imageModelArrayList = populateList()

        val recyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val mAdapter = SubmissionAdapter(imageModelArrayList)
        recyclerView.adapter = mAdapter
    }

    private fun populateList(): ArrayList<SubmissionModal> {
        val list = ArrayList<SubmissionModal>()

        val myImageList = arrayOf(
            R.drawable.ah, R.drawable.mg, R.drawable.sb
        )

        val myNameList = arrayOf("Abbey Hunt", "Mabel Griffith", "Szymon Booth")

        val myLevelList = arrayOf(10.2F, 7.8F, 2.9F)


        for (i in myImageList.indices) {
            val imageModel = SubmissionModal()
            imageModel.setName(myNameList[i])
            imageModel.setImage(myImageList[i])
            imageModel.setLevel(myLevelList[i])
            list.add(imageModel)
        }

        return list
    }
}