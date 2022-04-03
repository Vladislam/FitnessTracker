package com.example.fitnesstracker.util.converters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

object RunSnapshotConverter {
    fun fromBitmapToByteArray(bitmap: Bitmap?): ByteArray {
        ByteArrayOutputStream().also {
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, it)
            return it.toByteArray()
        }
    }

    fun fromByteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}