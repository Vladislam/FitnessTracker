package com.example.fitnesstracker.util.binding

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.fitnesstracker.R

@BindingAdapter("app:loadImage", "app:placeholder", requireAll = false)
fun ImageView.loadImage(byteArray: ByteArray?, placeholder: Drawable? = null) {
    Glide.with(this)
        .load(byteArray)
        .placeholder(
            placeholder ?: ContextCompat.getDrawable(
                this.context,
                R.drawable.no_image_placeholder
            )
        )
        .error(R.drawable.no_image_placeholder)
        .into(this)
}