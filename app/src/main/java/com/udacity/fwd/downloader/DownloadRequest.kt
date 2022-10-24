package com.udacity.fwd.downloader

import android.net.Uri

class DownloadRequest(
    url: String,
    val title: String = "",
    val description: String = ""
) {
    val uri = Uri.parse(url)
}