package com.udacity.fwd.downloader

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import com.udacity.fwd.downloader.databinding.ActivityMainBinding
import com.udacity.fwd.downloader.util.getDownloadManagerResult


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private val TAG = "MainActivity"
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    companion object {
        //        private const val URL =
//            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
        private const val NOTIFICATION_ID = 0
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)


            sendNotification(id)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        createChannel(CHANNEL_ID, "Downloads", "Downloaded Files")
//        setContentView(R.layout.activity_main)
//        setSupportActionBar(binding.toolbar)


        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.rdoGroup.setOnCheckedChangeListener { radioGroup, id ->
            binding.txtUrl.visibility = when (id == R.id.rdoOther) {
                true -> View.VISIBLE
                false -> View.INVISIBLE
            }

        }
        binding.customButton.setOnClickListener {
            if (binding.rdoGroup.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Please Select an option", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val downloadRequest = when (binding.rdoGroup.checkedRadioButtonId) {
                    binding.rdoGlide.id -> {
                        DownloadRequest(
                            binding.rdoGlide.tag.toString(),
                            getString(R.string.radio_glide_text),
                            getString(R.string.radio_glide_desc)
                        )
                    }
                    binding.rdoRetrofit.id -> {
                        DownloadRequest(
                            binding.rdoRetrofit.tag.toString(),
                            getString(R.string.radio_retrofit_text),
                            getString(R.string.radio_retrofit_desc)
                        )
                    }

                    binding.rdoLoadApp.id -> {
                        DownloadRequest(
                            binding.rdoLoadApp.tag.toString(),
                            getString(R.string.radio_loadadpp_text),
                            getString(R.string.radio_loadadpp_desc)
                        )
                    }
                    //Other (manual url)
                    else -> {
                        val url = binding.txtUrl.text.toString()
                        if (validateUrl(url)) {
                            DownloadRequest(
                                url,
                                getString(R.string.radio_other_text),
                                getString(R.string.radio_other_desc)
                            )
                        } else {
//                            binding.customButton.stopAnimation()
                            Toast.makeText(
                                this,
                                getString(R.string.invalid_url_error),
                                Toast.LENGTH_SHORT
                            ).show()
                            null
                        }
                    }

                }


                download(downloadRequest)
            }

        }
    }

    private fun validateUrl(url: String): Boolean {
        return URLUtil.isValidUrl(url)
    }

    private fun createChannel(channelId: String, channelName: String, desc: String) {
        val notiChannel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        notiChannel.apply {
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
            description = desc
            setShowBadge(true)
        }
        val notiMgr = getSystemService(NotificationManager::class.java)
        notiMgr.createNotificationChannel(notiChannel)


    }

    private fun download(dr: DownloadRequest?) {
        dr?.let {
            val request =
                DownloadManager.Request(dr.uri)
                    .setTitle(dr.title)
                    .setDescription(dr.description)
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)


            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        }


    }


    fun sendNotification(id: Long?) {
        notificationManager = getSystemService(NotificationManager::class.java)
        val actionintent = Intent(applicationContext, DetailActivity::class.java)
        var title = getString(R.string.notification_title)

        id?.let {
            actionintent.putExtra(DownloadManager.EXTRA_DOWNLOAD_ID, id)
            val result = getDownloadManagerResult(id, MainActivity@ this)
            title += "....." + result.status

        }


        pendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            actionintent,
            PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE
        )


        val notificationBuilder = NotificationCompat
            .Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(getString(R.string.notification_description))
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(0, getString(R.string.notification_button), pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

    }


//    fun cancelNotification() {
//        notificationManager =
//            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.cancelAll()
//    }


}
