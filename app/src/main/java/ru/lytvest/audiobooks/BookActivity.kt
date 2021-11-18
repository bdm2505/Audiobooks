package ru.lytvest.audiobooks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class BookActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        book?.let {
            val name = findViewById<TextView>(R.id.name_book)
            name.text = it.name
            findViewById<TextView>(R.id.author_book).text = it.author
            findViewById<TextView>(R.id.description_book).text = it.description
            Glide.with(this)
                .load(it.image)
                .placeholder(R.drawable.load)
                .error(R.drawable.ic_launcher_background)
                .into(findViewById(R.id.image_book))
        } ?: run {
            finish()
        }
    }

    companion object {
        var book: Book? = null
    }
}