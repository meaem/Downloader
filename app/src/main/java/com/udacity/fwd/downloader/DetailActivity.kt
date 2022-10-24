package com.udacity.fwd.downloader

import android.app.DownloadManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.fwd.downloader.databinding.ActivityDetailBinding
import com.udacity.fwd.downloader.util.getDownloadManagerResult

class DetailActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityDetailBinding>(this, R.layout.activity_detail)

        //close the current activity and open the main activity if not opened
        binding.button.setOnClickListener {
            this.finish()
            val newIntent = Intent(applicationContext, MainActivity::class.java)
            newIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(newIntent)
        }

        val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
//        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        val result = getDownloadManagerResult(downloadId, this)


        binding.txtID.text = downloadId.toString()
        binding.txtStatus.text = result.status
        binding.txtTitle.text = result.title
        binding.txtDesc.text = result.desc


    }



}
