package ru.lytvest.audiobooks

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.os.Build
import android.os.Bundle

import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import ru.lytvest.audiobooks.torrent.TorrentDownload
import java.io.File


@ExperimentalSerializationApi
fun main(){
    println(Json.decodeFromStream<List<Book>>(File("info.json").inputStream()))


}

class MainActivity : AppCompatActivity() {


    val CHANNEL_ID = "open-channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Debug.stopMethodTracing()

//        val io = CoroutineScope(SupervisorJob() + Dispatchers.IO)
//        io.launch {
//            TorrentDownload().startDownloading(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.absolutePath + "/file.torrent")
//        }
//        val rv = findViewById<RecyclerView>(R.id.rv)
//        rv.layoutManager = LinearLayoutManager(this)
//        rv.adapter = RVAdapter(this)

        val mediaSession = MediaSession(this, "MainActivity")
        mediaSession.setMetadata(
            MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, "currentTrack.title")

                .putString(MediaMetadata.METADATA_KEY_ARTIST, "currentTrack.artist")

                .putString(
                    MediaMetadata.METADATA_KEY_ALBUM_ART_URI, "currentTrack.albumArtUri")

                .putLong(MediaMetadata.METADATA_KEY_DURATION, 10000) // 4

                .build()
        )

        val mediaStyle = Notification.MediaStyle().setMediaSession(mediaSession.sessionToken)
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val n = Notification.Builder(this, CHANNEL_ID)
                .setStyle(mediaStyle)
                .setSmallIcon(R.drawable.play)
                .setContentTitle("title")
                .setAutoCancel(true)
                .setSubText("sub text")
                .setContentText("context text")



            val pauseAction: Notification.Action = Notification.Action.Builder(
                R.drawable.pause, "Pause", PendingIntent.getActivity(this,0, Intent(this, MainActivity::class.java), 0)
            ).build()

            n.addAction(pauseAction)
            n.build()

        } else {
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("title")
                .setContentText("Content")
                .setSmallIcon(R.drawable.play)
                .build()
        }





        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, "default channel", importance).apply {
                description = "descriptionText"
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        Log.d(this.javaClass.simpleName, "start notification")

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(123, notification)
        }


    }

}
//fun Context.readTxtFile(fileId: Int): String{
//    val stream = BufferedReader(InputStreamReader(resources.openRawResource(fileId)))
//    val sb = StringBuilder()
//    stream.use {
//        while (true){
//            sb.append(it.readLine() ?: break)
//        }
//    }
//    return sb.toString()
//}

@ExperimentalSerializationApi
class RVAdapter(val context: Context) : RecyclerView.Adapter<RVAdapter.BookViewHolder>() {


    val listItem = Json.decodeFromStream<List<Book>>(context.resources.openRawResource(R.raw.info))

    class BookViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.book_name)
        val author = view.findViewById<TextView>(R.id.book_author)
        val description = view.findViewById<TextView>(R.id.book_description)
        val image = view.findViewById<ImageView>(R.id.book_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val card = LayoutInflater.from(parent.context).inflate(R.layout.card, parent, false)
        return BookViewHolder(card)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = listItem[position]
        holder.name.text = book.firstLine
        holder.author.text = "${book.author}\n${book.reader}"
        holder.description.text = book.description
        Glide.with(context)
            .load(book.image)
            .placeholder(R.drawable.load)
            .error(R.drawable.ic_launcher_background)
            .into(holder.image)

        holder.view.setOnClickListener{
            val intent = Intent(context, BookActivity::class.java)
            intent.putExtra("book", book)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listItem.size
    }
}