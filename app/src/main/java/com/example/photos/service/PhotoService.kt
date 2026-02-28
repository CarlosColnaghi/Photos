package com.example.photos.service

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.example.photos.model.Photo
import com.google.gson.Gson
import java.net.HttpURLConnection.HTTP_NOT_MODIFIED
import java.net.HttpURLConnection.HTTP_OK

class PhotoService(context: Context) {
    companion object {
        const val ENDPOINT = "https://jsonplaceholder.typicode.com/photos"

        @Volatile
        private var INSTANCE: PhotoService? = null
        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: PhotoService(context).also {
                INSTANCE = it
            }
        }
    }

    private val requestQueue: RequestQueue by lazy{
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addRequestToQueue(request: Request<T>){
        requestQueue.add(request)
    }

    class FetchPhotos(
        private val responseListener: Response.Listener<MutableList<Photo>>,
        errorListener: Response.ErrorListener
    ): Request<MutableList<Photo>>(Method.GET, ENDPOINT, errorListener){
        override fun parseNetworkResponse(response: NetworkResponse?): Response<MutableList<Photo>?>? {
            return if(response?.statusCode == HTTP_OK || response?.statusCode == HTTP_NOT_MODIFIED){
                String(response.data).run{
                    Response.success(
                        Gson().fromJson(this, Array<Photo>::class.java).toMutableList(),
                        HttpHeaderParser.parseCacheHeaders(response)
                    )
                }
            }else{
                Response.error(VolleyError())
            }
        }

        override fun deliverResponse(response: MutableList<Photo>?) {
            responseListener.onResponse(response)
        }

    }

    private fun String.fixUrl() = "${this.replace("via.placeholder.com", "placehold.co")}/image.png"

    fun fetchPhotoPreview(
        photo: Photo,
        onSuccess: (Bitmap) -> Unit,
        onError: (VolleyError) -> Unit
    ){
        ImageRequest(
            photo.url.fixUrl(),
            {
                onSuccess(it)
            },
            0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888,
            {
                onError(it)
            }
        ).also {
            addRequestToQueue(it)
        }
    }

    fun fetchThumbnailPreview(
        photo: Photo,
        onSuccess: (Bitmap) -> Unit,
        onError: (VolleyError) -> Unit
    ){
        ImageRequest(
            photo.thumbnailUrl.fixUrl(),
            {
                onSuccess(it)
            },
            0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888,
            {
                onError(it)
            }
        ).also {
            addRequestToQueue(it)
        }
    }
}