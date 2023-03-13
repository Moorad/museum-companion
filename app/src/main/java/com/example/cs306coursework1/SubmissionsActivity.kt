package com.example.cs306coursework1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SubmissionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submissions)

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