package ru.lytvest.audiobooks

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.media.session.MediaButtonReceiver
import kotlinx.coroutines.*



class MusicPlayService : Service() {

    val NOTIFICATION_ID = 400
    val DEFAULT_CHANNEL_ID = "default_channel"

    val stateBuilder = PlaybackStateCompat.Builder().setActions(
            PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_STOP
                    or PlaybackStateCompat.ACTION_PAUSE
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        )



    //lateinit var mediaPlayer: MediaPlayer
    //lateinit var manager: MediaSessionManager
    lateinit var session: MediaSessionCompat
    lateinit var controller: MediaController
    var curr_id = 0


    var job: Job? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun initPlayer(){
        //mediaPlayer = MediaPlayer()
        //manager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        session = MediaSessionCompat(this, "Media Session")

        session.setCallback(callbacks)

        val context = applicationContext

        val activityIntent = Intent(context, MainActivity::class.java)
        session.setSessionActivity(PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE))

        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON, null, context, androidx.media.session.MediaButtonReceiver::class.java)
        session.setMediaButtonReceiver(PendingIntent.getBroadcast(context, 0, mediaButtonIntent, PendingIntent.FLAG_IMMUTABLE))

        toast("init player")

    }

    fun toast(s: String){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }
    private val callbacks = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            super.onPlay()
            toast("play $curr_id")
        }

        override fun onPause() {
            super.onPause()

            toast("pause $curr_id")
        }

        override fun onSkipToNext() {
            super.onSkipToNext()

            toast("play next ${++curr_id}")
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()

            toast("play previous ${--curr_id}")
        }

        override fun onStop() {
            super.onStop()

            toast("toast $curr_id")
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)

            toast("seek $curr_id $pos")
        }
    }

    override fun onCreate() {
        super.onCreate()
        initPlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(session, intent)
        toast("on start command")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        toast("destroy")
        session.release()
    }
}