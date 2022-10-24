package com.udacity.fwd.downloader.util

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import com.udacity.fwd.downloader.DownloadManagerResult
import com.udacity.fwd.downloader.R


@SuppressLint("Range")
fun getDownloadManagerResult(
    downloadId: Long, context: Context
): DownloadManagerResult {
    //query the DM with the download id
    val query = DownloadManager.Query().setFilterById(downloadId)
    val downloadManager = context.getSystemService(DownloadManager::class.java)
    // open the cursor and get the first result
    val cursor: Cursor = downloadManager.query(query)
    cursor.moveToFirst()
    val status = getStatusString(
        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)),
        context
    )
    val title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
    val desc = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION))
    cursor.close()
    return DownloadManagerResult(downloadId, status, title, desc)
}


fun getStatusString(status: Int, context: Context): String {

    return when (status) {
        DownloadManager.STATUS_FAILED -> context.getString(R.string.failed)
        DownloadManager.STATUS_PAUSED -> context.getString(R.string.paused)
        DownloadManager.STATUS_PENDING -> context.getString(R.string.pending)
        DownloadManager.STATUS_RUNNING -> context.getString(R.string.downloading_in_progress)
        DownloadManager.STATUS_SUCCESSFUL -> context.getString(R.string.success)
        else -> context.getString(R.string.invalid_status_code)
    }
}
