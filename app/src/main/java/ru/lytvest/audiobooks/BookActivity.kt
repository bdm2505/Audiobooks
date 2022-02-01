package ru.lytvest.audiobooks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class BookActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)
        val book = savedInstanceState?.getParcelable<Book>("book") ?: return

        findViewById<Button>(R.id.listen_button).setOnClickListener {
            val intent = Intent(this, DownloadActivity::class.java)
            intent.putExtra("book", book)
            startActivity(intent)
        }



        val name = findViewById<TextView>(R.id.name_book)
        name.text = book.name
        findViewById<TextView>(R.id.author_book).text = book.author
        findViewById<TextView>(R.id.description_book).text = book.description
        Glide.with(this)
            .load(book.image)
            .placeholder(R.drawable.load)
            .error(R.drawable.ic_launcher_background)
            .into(findViewById(R.id.image_book))

    }

}