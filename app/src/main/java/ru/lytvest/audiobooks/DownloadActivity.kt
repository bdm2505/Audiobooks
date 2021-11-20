package ru.lytvest.audiobooks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.TextView
import com.github.se_bastiaan.torrentstream.StreamStatus
import com.github.se_bastiaan.torrentstream.Torrent
import com.github.se_bastiaan.torrentstream.TorrentOptions
import com.github.se_bastiaan.torrentstream.TorrentStream
import com.github.se_bastiaan.torrentstream.listeners.TorrentListener
import java.lang.Exception

class TListener(val view: TextView) : TorrentListener {
    override fun onStreamPrepared(torrent: Torrent?) {
        view.text =  "${view.text}\nprepared $torrent"
    }

    override fun onStreamStarted(torrent: Torrent?) {
        view.text =  "${view.text}\nstarted $torrent"
    }

    override fun onStreamError(torrent: Torrent?, e: Exception?) {
        view.text =  "${view.text}\nerror $torrent ${e?.message}"
    }

    override fun onStreamReady(torrent: Torrent?) {
        view.text =  "${view.text}\nready $torrent"
    }

    override fun onStreamProgress(torrent: Torrent?, status: StreamStatus?) {
        //if(Math.random() < 0.5)
            view.text =  "${view.text}\nprogress $torrent $status"
    }

    override fun onStreamStopped() {
        view.text =  "${view.text}\nstopped! "
    }

}

class DownloadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        val options = TorrentOptions.Builder()
            .saveLocation(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS))
            .removeFilesAfterStop(true)
            .build()

        val torrent = TorrentStream.init(options)
        torrent.addListener(TListener(findViewById(R.id.infoText)))
        torrent.startStream("android.resource://" + packageName + "/file.torrent" )
    }
}