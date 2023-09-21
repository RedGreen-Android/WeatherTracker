package com.example.weathertrackerapp.util

import android.widget.ImageView
import com.squareup.picasso.Picasso

/***
 * This was an attempt to try displaying the ICON using
 * "Data Binding" using Picasso. Due to Time constrains, went with Glide as I always do
 */
class ImageProviderUtil {
    private val picasso = Picasso.get()

    fun loadUrl(url: String, imageView: ImageView) {
        picasso.load(url).into(imageView)
    }
}