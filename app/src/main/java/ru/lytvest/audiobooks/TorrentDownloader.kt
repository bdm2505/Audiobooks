package ru.lytvest.audiobooks

import android.net.Uri
import android.os.Environment
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.frostwire.jlibtorrent.TorrentHandle
import com.masterwok.simpletorrentandroid.TorrentSession
import com.masterwok.simpletorrentandroid.TorrentSessionOptions
import kotlinx.coroutines.*
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtil
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.Files
import java.util.zip.ZipFile

typealias TDListener = (List<String>, List<Double>) -> Unit

class TorrentDownloader(
    val activity: AppCompatActivity,
    val book: Book,
    val main: CoroutineScope,
    val io: CoroutineScope
) {


    val listeners = mutableListOf<TDListener>()

    fun addListener(listener: TDListener) =
        listeners.add(listener)

    init {

        val status = activity.findViewById<TextView>(R.id.statusView)
        val progressBar = activity.findViewById<ProgressBar>(R.id.progressBar)
        val percent = activity.findViewById<TextView>(R.id.percent)
        val speed = activity.findViewById<TextView>(R.id.speed)


        main.launch {
            status.text = "file loading..."
            val file = withContext(Dispatchers.IO) {
                try {
                    val directory =
                        activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!
                    val file = File(directory, "torr.zip")
                    println("file (${file.isFile}) ${file.toURI()}")
                    FileUtils.copyURLToFile(URL(book.torrent), file)
                    try {
                        val zip = ZipFile(file)
                        unzipFile(zip, directory)
                    } catch (e: Exception) {
                        System.err.println("error ${e.message}")
                        val zip = ZipFile(file, Charset.forName("CP866"))
                        unzipFile(zip, directory)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            status.text = "file save ${file?.toURI() ?: "error"}"

            if (file == null) {
                return@launch
            }

            val torrentUri = Uri.fromFile(file)


            val torrentSessionOptions = TorrentSessionOptions(
                downloadLocation = activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!,
                enableLogging = false,

                shouldStream = true
            )

            val torrentSession = TorrentSession(torrentSessionOptions)
            

            torrentSession.listener = TListener(main) { handle ->


                status.text = "downloading..."
                val pr = handle.status().totalWantedDone().toDouble() / handle.status().totalWanted()
                progressBar.progress = (pr * 100).toInt()
                percent.text = "${(pr * 1000).toInt() / 10.0}%"

                val arr = handle.fileProgress() // для быстроты (TorrentHandle.FileProgressFlags.PIECE_GRANULARITY)
                val torrentFile = handle.torrentFile()
                val fs = torrentFile?.files() ?: return@TListener


                val files = List(arr.size) {
                    torrentSessionOptions.downloadLocation.path + "/" + fs.filePath(it)
                }
                val percents = List(arr.size) { arr[it].toDouble() / fs.fileSize(it) }

                //status.text = files.map { "file: $it\n" }.toString()

                for (lis in listeners)
                    lis(files, percents)

//                if (handle.status().isFinished) {
//                    torrentSession.stop()
//                    status.text = "Ok!"
//                }

            }

            io.launch {
                torrentSession.start(activity, torrentUri)
                while (torrentSession.isRunning) {
                    delay(1000L)
                    main.launch {
                        speed.text = "${torrentSession.downloadRate / 1024}Kb"
                    }
                }
            }
        }
    }

    private fun unzipFile(zip: ZipFile, directory: File): File? {
        val entries = zip.entries()
        while (entries.hasMoreElements()) {
            val en = entries.nextElement()!!
            val endFile = File(directory, en.name)
            println("unzip file: ${endFile.toURI()}")
            IOUtil.copy(zip.getInputStream(en), FileOutputStream(endFile))
            return endFile
        }
        return null
    }

}