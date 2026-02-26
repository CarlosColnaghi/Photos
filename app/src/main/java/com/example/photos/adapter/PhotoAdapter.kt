package com.example.photos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.photos.model.Photo

class PhotoAdapter(
    private val activityContext: Context,
    private val photos: MutableList<Photo>
): ArrayAdapter<Photo>(
    activityContext,
    android.R.layout.simple_list_item_1,
    photos
) {
    private data class PhotoHolder(val titleTextView: TextView)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(activityContext)
            .inflate(android.R.layout.simple_list_item_1, parent, false).apply {
                tag = PhotoHolder(findViewById(android.R.id.text1))
            }
        (view.tag as PhotoHolder).titleTextView.text = photos[position].title
        return view
    }
}