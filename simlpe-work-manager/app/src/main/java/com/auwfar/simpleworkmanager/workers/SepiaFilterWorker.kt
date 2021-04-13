package com.auwfar.simpleworkmanager.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.auwfar.simpleworkmanager.utils.ImageUtils
import java.io.FileOutputStream

class SepiaFilterWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        val imagePath = inputData.getString("image_path") ?: return Result.failure()
        val bitmap = BitmapFactory.decodeFile(imagePath)
        val sepiaBitmap = ImageUtils.applySepiaFilter(bitmap)

        val outputStream = FileOutputStream(imagePath)
        outputStream.use { output ->
            sepiaBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            output.flush()
        }

        val output = workDataOf("image_path" to imagePath)
        return Result.success(output)
    }

}