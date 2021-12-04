package ru.lytvest.audiobooks

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtil
import ru.lytvest.audiobooks.torrent.TorrentDownload
import ru.lytvest.audiobooks.torrent.TorrentDownloadDListener
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.charset.Charset
import java.util.zip.ZipFile
import kotlin.math.max
import kotlin.math.min


class DownloadActivity : AppCompatActivity() {
    val T = javaClass.simpleName

    val main = MainScope()
    val io = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    var files = listOf<String>()
    var percents = listOf<Double>()

    var buttonPlayPressed = false
    var audioPlay = false
    var currIndex = 0
    var currPlayingIndex = -1

    lateinit var previousTwoButton: ImageButton
    lateinit var previousOneButton: ImageButton
    lateinit var playButton: ImageButton
    lateinit var nextOneButton: ImageButton
    lateinit var nextTwoButton: ImageButton

    lateinit var audioBar: SeekBar
    lateinit var audioDownloadBar: ProgressBar

    var timerUpdater: Job? = null

    val audio = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        audioBar = findViewById(R.id.audioBar)
        audioDownloadBar = findViewById(R.id.audioDownloadBar)
        val percentView = findViewById<TextView>(R.id.percent)
        val speedView = findViewById<TextView>(R.id.speed)

        BookActivity.book?.let {
            val me = this
            Log.d(T, "check book $it")
            val listener = object : TorrentDownloadDListener() {
                override fun updatePercents(percents: List<Double>) {
                    main.launch {
                        me.files = files
                        me.percents = percents
                        if (percents.size > currIndex) {
                            audioDownloadBar.setProgress((percents[currIndex] * 100).toInt(), true)
                        }
                        startAudio()
                    }
                }

                override fun percentChanged() {
                    main.launch {
                        percentView.text = percentToString()
                        speedView.text = downloadRateToString()
                    }
                }
            }
            Log.d(T, "launch load and unzip")
            io.launch {
                val file = loadAndUnzipFile(
                    it.torrent,
                    getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: return@launch
                )
                Log.d(T, "file unzipped $file")
                TorrentDownload(file ?: return@launch, listener, io)
            }
        }

        previousOneButton = findViewById(R.id.previous_one)
        previousTwoButton = findViewById(R.id.previous_two)
        playButton = findViewById(R.id.playButton)
        nextOneButton = findViewById(R.id.next_one)
        nextTwoButton = findViewById(R.id.next_two)

        previousTwoButton.setOnClickListener {
            if(currIndex > 0){
                currIndex -= 1
                stopAudio()
                startAudio()
            }
        }
        previousOneButton.setOnClickListener {
            audio.seekTo(max(audio.currentPosition - 30000, 0))
            audioBarSetCurrentPosition()
        }
        nextOneButton.setOnClickListener {
            audio.seekTo(min(audio.currentPosition + 30000, audio.duration))
            audioBarSetCurrentPosition()
        }
        nextTwoButton.setOnClickListener {
            if(currIndex < files.size - 1){
                currIndex += 1
                stopAudio()
                startAudio()
            }
        }
        playButton.setOnClickListener {
            if(buttonPlayPressed){
                playButton.setImageResource(R.drawable.play)
                stopAudio()
            } else{
                playButton.setImageResource(R.drawable.pause)
                startAudio()
            }
            buttonPlayPressed = !buttonPlayPressed

        }

        audioBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar == null)
                    return

                audio.seekTo((seekBar.progress / 100.0 * audio.duration).toInt())
            }

        })

        Glide.with(this)
            .load(BookActivity.book?.image ?: "no image")
            .placeholder(R.drawable.load)
            .error(R.drawable.ic_launcher_background)
            .into(findViewById(R.id.image))
    }
    fun stopAudio(){
        audio.pause()
        audioPlay = false
        if (currPlayingIndex != currIndex){
            audio.stop()
            if (percents.size > currIndex){
                audioDownloadBar.setProgress((percents[currIndex] * 100).toInt(), true)
            }
        }
    }

    fun startAudio() {
        try {
            if (buttonPlayPressed && !audioPlay && percents[currIndex] == 1.0) {
                println("start audio index=${currIndex}")
                if (currPlayingIndex != currIndex) {
                    audio.reset()
                    println("play music in ${files[currIndex]}")
                    audio.setDataSource(files[currIndex])
                    currPlayingIndex = currIndex
                    audio.prepare()
                }
                audio.start()
                audioPlay = true
                runTimerUpdater()
            }
        } catch (e: Exception){
            e.printStackTrace()
        }

    }

    fun runTimerUpdater(){
        if(timerUpdater == null) {
            timerUpdater = io.launch {
                while (true) {
                    audioBarSetCurrentPosition()
                    delay(300L)
                }
            }
        }
    }

    private fun audioBarSetCurrentPosition() {
        main.launch {
            audioBar.setProgress(
                (audio.currentPosition.toDouble() / audio.duration * 100).toInt(),
                true
            )
        }
    }


    suspend fun loadAndUnzipFile(url: String, directory: File): File? {
        val file = withContext(Dispatchers.IO) {
            try {
                val file = File(directory, "torr.zip")
                println("file (${file.isFile}) ${file.toURI()}")
                FileUtils.copyURLToFile(URL(url), file)
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
        return file
    }

    fun unzipFile(zip: ZipFile, directory: File): File? {
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