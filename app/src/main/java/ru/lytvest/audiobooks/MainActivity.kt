package ru.lytvest.audiobooks

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rv = findViewById<RecyclerView>(R.id.rv)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = RVAdapter(this)

    }

}

class RVAdapter(val context: Context) : RecyclerView.Adapter<RVAdapter.BookViewHolder>() {


    val listItem = listOf(
        Book("Дорога в маги", "Валери я вв", "https://cm.author.today/content/2021/11/15/8fe8ef9b8f75424da72b3a90eef95a96.jpg?width=265&height=400&mode=max", "Жил жлв был герой такой оолы ыооо в ..."),
        Book("Дорога в маги 2", "Валери я вв", "ссылка", "Жил жлв был герой такой оолы ыооо в ..."),
        Book("Дорога в маги 3", "Валери я вв", "ссылка", "Жил жлв был герой такой оолы ыооо в ..."),
        Book("Дорога в маги 4", "Валери я вв", "ссылка", "Жил жлв был герой такой оолы ыооо в ..."),
        Book("Дорога в маги", "Валери я вв", "ссылка", "Жил жлв был герой такой оолы ыооо в ..."),
        Book("Дорога в маги 2", "Валери я вв", "ссылка", "Жил жлв был герой такой оолы ыооо в ..."),
        Book("Дорога в маги 3", "Валери я вв", "ссылка", "Жил жлв был герой такой оолы ыооо в ..."),
        Book("Дорога в маги 4", "Валери я вв", "ссылка", "Жил жлв был герой такой оолы ыооо в ..."),
        Book("Дорога в маги", "Валери я вв", "ссылка", "Жил жлв был герой такой оолы ыооо в ..."),
        Book("Дорога в маги 2", "Валери я вв", "ссылка", "Жил жлв был герой такой оолы ыооо в ..."),
        Book("Дорога в маги 3", "Валери я вв", "ссылка", "Жил жлв был герой такой оолы ыооо в ..."),
        Book("Дорога в маги 4", "Валери я вв", "ссылка", "Жил жлв был герой такой оолы ыооо в ..."),
        Book("Дорога в маги", "Валери я вв", "ссылка", "Жил жлв был герой такой оолы ыооо в ..."),
        Book("Дорога в маги 2", "Валери я вв", "ссылка", "Жил жлв был герой такой оолы ыооо в ..."),
        Book("Дорога в маги 3", "Валери я вв", "ссылка", "Жил жлв был герой такой оолы ыооо в ..."),
        Book("Дорога в маги 4", "Валери я вв", "ссылка", "Жил жлв был герой такой оолы ыооо в ..."),
    )

    class BookViewHolder(view: View): RecyclerView.ViewHolder(view) {
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
        holder.name.text = book.name
        holder.author.text = book.author
        holder.description.text = book.description
        Glide.with(context)
            .load(book.image)
            .placeholder(R.drawable.load)
            .error(R.drawable.ic_launcher_background)
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return listItem.size
    }
}