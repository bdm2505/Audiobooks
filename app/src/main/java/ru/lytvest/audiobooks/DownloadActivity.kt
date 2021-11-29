package ru.lytvest.audiobooks

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import kotlin.math.max
import kotlin.math.min


class DownloadActivity : AppCompatActivity() {

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

        BookActivity.book?.let {
            var firstFileEnd = false
            TorrentDownloader(this, it, main, io).addListener { files, percents ->

                    this.files = files
                    this.percents = percents
                    if (percents.size > currIndex){
                        audioDownloadBar.setProgress((percents[currIndex] * 100).toInt(), true)
                    }
                    startAudio()

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
        }
        nextOneButton.setOnClickListener {
            audio.seekTo(min(audio.currentPosition + 30000, audio.duration))
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
            if(buttonPlayPressed && !audioPlay && percents[currIndex] == 1.0) {
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

    }

    fun runTimerUpdater(){
        if(timerUpdater == null) {
            timerUpdater = io.launch {
                while (true) {
                    audioBar.setProgress(
                        (audio.currentPosition.toDouble() / audio.duration * 100).toInt(),
                        true
                    )
                    delay(1000L)
                }
            }
        }
    }


}