package com.auwfar.simpleworkmanager.ui

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.auwfar.simpleworkmanager.R
import kotlinx.android.synthetic.main.activity_main.*
import com.auwfar.simpleworkmanager.workers.DownloadWorker
import com.auwfar.simpleworkmanager.workers.FileClearWorker
import com.auwfar.simpleworkmanager.workers.SepiaFilterWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        downloadImage()
    }

    private fun downloadImage() {
        val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiresStorageNotLow(true)
                .setRequiredNetworkType(NetworkType.NOT_ROAMING)
                .build()

        val fileClearWorker = OneTimeWorkRequestBuilder<FileClearWorker>()
                .build()

        val downloadWorker = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setConstraints(constraints)
                .build()

        val sepiaFilterWorker = OneTimeWorkRequestBuilder<SepiaFilterWorker>()
                .setConstraints(constraints)
                .build()

        val workManager = WorkManager.getInstance(this)
        workManager.beginWith(fileClearWorker)
                .then(downloadWorker)
                .then(sepiaFilterWorker)
                .enqueue()

        workManager.getWorkInfoByIdLiveData(sepiaFilterWorker.id).observe(this, { info ->
            if (info.state.isFinished) {
                val imagePath = info.outputData.getString("image_path")

                if (!imagePath.isNullOrEmpty()) {
                    displayImage(imagePath)
                }
            }
        })
    }

    private fun displayImage(imagePath: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val bitmap = loadImageFromFile(imagePath)

            image.setImageBitmap(bitmap)
        }
    }

    private suspend fun loadImageFromFile(imagePath: String) = withContext(Dispatchers.IO) {
        BitmapFactory.decodeFile(imagePath)
    }
}