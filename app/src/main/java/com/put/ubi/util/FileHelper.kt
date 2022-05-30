package com.put.ubi.util

import android.content.Context
import android.os.Environment
import android.os.Environment.DIRECTORY_DOCUMENTS
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject

class FileHelper @Inject constructor(@ApplicationContext context: Context) {
    fun saveInExternalStorage(filename: String, data: ByteArray): File {
        val file = File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS), "$filename")
        FileOutputStream(file).use {
            it.write(data)
        }
        return file
    }

    fun readFromExternalStorage(filename: String): FileInputStream {
        val file = File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS), "/PPK/$filename")
        return file.inputStream()
    }
}