package org.wit.leeway.helpers

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import org.wit.leeway.R

fun showImagePicker(intentLauncher : ActivityResultLauncher<Intent>) {

    var chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
    chooseFile.type = "image/*"
    chooseFile = Intent.createChooser(chooseFile, R.string.select_habit_image.toString())
    //chooseFile.flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
    intentLauncher.launch(chooseFile)
}

fun readImageFromPath(context: android.content.Context, path: String) : Bitmap? {
    var bitmap : Bitmap? = null
    val uri = Uri.parse(path)

    if(uri != null) {
        try{
            val parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.getFileDescriptor()
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()
        } catch (e: Exception){

        }
    }
    return bitmap
}