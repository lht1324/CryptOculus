package org.techtown.cryptoculus.repository.model

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

class ImageFileHandler(private val cacheDir: File) {
    fun saveImageFile(bitmap: Bitmap, fileName: String) {
        val tempFile = File(cacheDir, fileName)

        tempFile.createNewFile()

        val fileOutputStream = FileOutputStream(tempFile)

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)

        fileOutputStream.close()
    }

    fun getImageFile(fileName: String): File? {
        val files = File(cacheDir.toString()).listFiles()
        val tempFile = File(cacheDir, fileName)

        return if (files.contains(tempFile))
            tempFile
        else
            null
    }
}