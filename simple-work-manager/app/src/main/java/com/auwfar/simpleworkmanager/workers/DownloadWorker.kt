package com.auwfar.simpleworkmanager.workers

import android.content.Context
import android.os.Environment
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class DownloadWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        val imageUrl = URL("https://wallpaperplay.com/walls/full/1/c/7/38027.jpg")
        val connection = imageUrl.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()

        val imagePath = "owl_image_${System.currentTimeMillis()}.jpg"
        val inputStream = connection.inputStream
        val directory = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(directory, imagePath)

        directory?.mkdir()
        val outputStream = FileOutputStream(file)
        outputStream.use { output ->
            val buffer = ByteArray(4 * 1024)

            var byteCount = inputStream.read(buffer)

            while (byteCount > 0) {
                output.write(buffer, 0, byteCount)

                byteCount = inputStream.read(buffer)
            }

            output.flush()
        }

        val output = workDataOf("image_path" to file.absolutePath)
        return Result.success(output)
    }

}