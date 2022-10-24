package com.udacity.fwd.downloader

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.fwd.downloader.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityDetailBinding>(this, R.layout.activity_detail)
//        setContentView()
//        setSupportActionBar(binding.toolbar)
        binding.button.setOnClickListener {
            this.finish()
            val newIntent = Intent(applicationContext, MainActivity::class.java)
            newIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(newIntent)
        }
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager


        val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        val query = DownloadManager.Query().setFilterById(downloadId)

        val cursor: Cursor = downloadManager.query(query)
        cursor.moveToFirst()
        val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
        val title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
        val desc = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION))
//        val localURI = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))


        cursor.close()

        binding.txtID.text = downloadId.toString()
        binding.txtStatus.text = getStatusString(status)
        binding.txtTitle.text = title.toString()
        binding.txtDesc.text = desc.toString()
//        binding.txtLocalURI.text = localURI.toString()


    }


    private fun getStatusString(status: Int): String {

        return when (status) {
            DownloadManager.STATUS_FAILED -> getString(R.string.failed)
            DownloadManager.STATUS_PAUSED -> getString(R.string.paused)
            DownloadManager.STATUS_PENDING -> getString(R.string.pending)
            DownloadManager.STATUS_RUNNING -> getString(R.string.downloading_in_progress)
            DownloadManager.STATUS_SUCCESSFUL -> getString(R.string.success)
            else -> getString(R.string.invalid_status_code)
        }

    }

}
