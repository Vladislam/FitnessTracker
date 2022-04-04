package com.example.fitnesstracker.util.binding

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.example.fitnesstracker.R
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

@BindingAdapter("app:loadImage", "app:placeholder", requireAll = false)
fun ImageView.loadImage(byteArray: ByteArray?, placeholder: Drawable? = null) {
    val shimmer = Shimmer.AlphaHighlightBuilder()
        .setDuration(1800)
        .setBaseAlpha(0.7f)
        .setHighlightAlpha(0.6f)
        .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
        .setAutoStart(true)
        .build()

    val shimmerDrawable = ShimmerDrawable().apply {
        setShimmer(shimmer)
    }

    Glide.with(this)
        .load(byteArray)
        .override(SIZE_ORIGINAL)
        .placeholder(placeholder ?: shimmerDrawable)
        .error(R.drawable.no_image_placeholder)
        .into(this)
}