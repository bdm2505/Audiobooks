package ru.lytvest.audiobooks.torrent

import android.util.Log
import com.frostwire.jlibtorrent.*

import com.frostwire.jlibtorrent.alerts.*
import kotlinx.coroutines.*
import java.io.File

abstract class TorrentDownloadDListener {
    val TAG = "MyTorrentListener"
    var files: List<String> = List(0) { "" }
    var baseDir: String = ""
    var persent = 0.0
    var downloadRate = 0

    fun setSaveDirectory(dir: String) {
        baseDir = dir
    }



    open fun started(handle: TorrentHandle) {
        Log.d(TAG, "torrent started!")
        handle.status().calculatePercent()
        handle.torrentFile()?.let { torrentInfo ->
            val fs = torrentInfo.files()
            files = List(fs.numFiles()) {
                baseDir + "/" + fs.filePath(it)
            }
            Log.d(TAG, "files = $files")
            val percents = List(fs.numFiles()) { 0.0 }
            setPriorities(fs, percents, torrentInfo, handle)
            updatePercents(percents)
        }
        percentChanged()

    }

    private fun setPriorities(
        fs: FileStorage,
        percents: List<Double>,
        torrentInfo: TorrentInfo,
        handle: TorrentHandle
    ) {
        for (index in 0..fs.numFiles() - 3) {
            if (percents[index] >= 1.0)
                continue
            val startIndex = fs.fileOffset(index) / torrentInfo.pieceLength()
            val endIndex = fs.fileOffset(index + 1) / torrentInfo.pieceLength()
            for (i in startIndex..endIndex)
                handle.piecePriority(i.toInt(), Priority.SEVEN)
            val startIndex2 = fs.fileOffset(index + 1) / torrentInfo.pieceLength()
            val endIndex2 = fs.fileOffset(index + 2) / torrentInfo.pieceLength()
            for (i in startIndex2..endIndex2)
                handle.piecePriority(i.toInt(), Priority.TWO)
            break
        }
    }

    open fun blockDownloading(handle: TorrentHandle) {
        handle.status().calculatePercent()
        downloadRate = handle.status().downloadRate()
        percentChanged()
    }
    open fun blockFinished(handle: TorrentHandle) {
        handle.status().calculatePercent()
        val arr = handle.fileProgress() // для быстроты (TorrentHandle.FileProgressFlags.PIECE_GRANULARITY)
        handle.torrentFile()?.let { torrentInfo ->
            val fs = torrentInfo.files()
            val percents = List(arr.size) { arr[it].toDouble() / fs.fileSize(it) }
            if (percents.size != files.size) {
                Log.e(TAG, "percents.size(${percents.size}) != files.size(${files.size})")
                return@let
            }
            setPriorities(fs, percents, torrentInfo, handle)
            updatePercents(percents)
            Log.d(TAG, "block finish ${percentToString()} rate=${downloadRateToString()} name=${handle.name()} percents=$percents")
        }
        downloadRate = handle.status().downloadRate()

        percentChanged()
    }

    abstract fun updatePercents(percents: List<Double>)

    open fun percentChanged(){}

    open fun finished() {
        persent = 1.0
        percentChanged()
    }

    private fun TorrentStatus.calculatePercent(){
        persent = this.totalWantedDone().toDouble() / this.totalWanted()
    }

    fun percentToString():String = "${(persent * 1000).toInt() / 10.0}%"
    fun downloadRateToString(): String = if (downloadRate > 1024 * 1024) "${downloadRate / 1024 / 1024}Mb/s" else "${downloadRate / 1024}Kb/s"
}

class TorrentDownload(val file: File, val listener: TorrentDownloadDListener, val io: CoroutineScope) {

    val manager = SessionManager()

    var job: Job? = null

    init {
        manager.addListener(object : AlertListener {
            override fun types(): IntArray? {
                return null
            }

            override fun alert(alert: Alert<*>) {
                when (alert.type()) {
                    AlertType.ADD_TORRENT -> {
                        val handle = (alert as AddTorrentAlert).handle()
                        listener.started(handle)
                        handle.resume()
                    }
                    AlertType.BLOCK_DOWNLOADING -> {
                        val a = alert as BlockDownloadingAlert
                        listener.blockDownloading(a.handle())
                    }
                    AlertType.PIECE_FINISHED -> {
                        val a = alert as PieceFinishedAlert
                        listener.blockFinished(a.handle())
                    }
                    AlertType.TORRENT_FINISHED -> {
                        listener.finished()
                        manager.stop()
                        job?.cancel()
                    }
                    else -> Unit
                }
            }
        })
        //job = io.launch {
        listener.setSaveDirectory(file.parentFile?.absolutePath ?: "")
            manager.start()
            val ti = TorrentInfo(file)
            manager.download(ti, file.parentFile)
        //}
    }
}