package com.example.weathertrackerapp.model

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.weathertrackerapp.util.ImageProviderUtil

/***
 * This was an attempt to try displaying the ICON using
 * "Data Binding" using Picasso. Decided to leave this to show the failed hacks
 */
data class IconData(
    val icon : String? = null
)
{
    companion object {
        @BindingAdapter("loadImage")
        @JvmStatic
        fun loadImage(imageView: ImageView, imageUrl: String) {
            if (imageUrl.isNotEmpty()) {
                Log.d("MyApp", "Loading image from URL: $imageUrl")
                ImageProviderUtil().loadUrl(imageUrl, imageView)
            }
        }
    }
}
