package com.auwfar.simpleworkmanager.workers

import android.content.Context
import android.os.Environment
import androidx.work.Worker
import androidx.work.WorkerParameters

class FileClearWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        val root = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return try {
            root?.listFiles()?.forEach { child ->
                if (child.isDirectory) {
                    child.deleteRecursively()
                } else {
                    child.delete()
                }
            }
            Result.success()
        } catch (error: Throwable) {
            error.printStackTrace()
            Result.failure()
        }

    }

}