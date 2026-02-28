package com.example.photos

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.photos.adapter.PhotoAdapter
import com.example.photos.databinding.ActivityMainBinding
import com.example.photos.model.Photo
import com.example.photos.service.PhotoService

class MainActivity : AppCompatActivity() {
    private val activityMainBinding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val photos: MutableList<Photo> = mutableListOf()

    private val photoAdapter: PhotoAdapter by lazy{
        PhotoAdapter(this, photos)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)
        setSupportActionBar(activityMainBinding.toolbar.root)
        supportActionBar?.apply {
            title = getString(R.string.photos)
        }

        activityMainBinding.titleSpinner.apply {
            adapter = photoAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    fetchPhotoPreview(photos[position])
                    fetchThumbnailPreview(photos[position])
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
        fetchPhotos()
    }

    private fun fetchPhotos() =
        PhotoService.FetchPhotos(
            {
                photoAdapter.addAll(it)
            },
            {
                Toast.makeText(this, getString(R.string.request_error), Toast.LENGTH_SHORT).show()
            }
        ).also{
            PhotoService.getInstance(this).addRequestToQueue(it)
        }

    private fun fetchPhotoPreview(photo: Photo) =
        PhotoService.getInstance(this).fetchPhotoPreview(
            photo,
            {
                activityMainBinding.photoImageView.setImageBitmap(it)
            },
            {
                Toast.makeText(this, getString(R.string.request_error), Toast.LENGTH_SHORT).show()
            }
        )

    private fun fetchThumbnailPreview(photo: Photo) =
        PhotoService.getInstance(this).fetchThumbnailPreview(
            photo,
            {
                activityMainBinding.thumbnailImageView.setImageBitmap(it)
            },
            {
                Toast.makeText(this, getString(R.string.request_error), Toast.LENGTH_SHORT).show()
            }
        )
}