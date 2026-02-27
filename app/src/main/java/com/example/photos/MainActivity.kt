package com.example.photos

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
        }

        getPhotos()
    }

    private fun getPhotos() =
        PhotoService.PhotosRequest(
            {
                photoAdapter.addAll(it)
            },
            {
                Toast.makeText(
                    this,
                    getString(R.string.request_error),
                    Toast.LENGTH_SHORT)
                .show()
            }
        ).also{
            PhotoService.getInstance(this).addRequestToQueue(it)
        }
}