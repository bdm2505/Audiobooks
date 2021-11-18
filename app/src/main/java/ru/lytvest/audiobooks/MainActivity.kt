package ru.lytvest.audiobooks

import android.content.Context
import android.content.Intent
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
        Book("Дорога в маги", "Глушановский Алексей", "http://lytvest.com.xsph.ru/wp-content/uploads/2021/11/doroga-v-magi-1.jpg", "Увлечение магией в нашем, не приспособленном для колдовства мире, может завести далеко. Очень далеко! Прямо таки в мир иной. Как в буквальном, так и в фигуральном смысле. Магия может помочь воскреснуть после нелепой гибели, познакомить с прекрасной богиней огня, дать в дар могучее тело демона. Но в этой жизни за все надо платить. И некоторые дары, несмотря на несомненную полезность, бывают отнюдь не безобидны. Увлечение магией в нашем, не приспособленном для колдовства мире, может завести далеко. Очень далеко! Прямо таки в мир иной. Как в буквальном, так и в фигуральном смысле. Магия может помочь воскреснуть после нелепой гибели, познакомить с прекрасной богиней огня, дать в дар могучее тело демона. Но в этой жизни за все надо платить. И некоторые дары, несмотря на несомненную полезность, бывают отнюдь не безобидны. Увлечение магией в нашем, не приспособленном для колдовства мире, может завести далеко. Очень далеко! Прямо таки в мир иной. Как в буквальном, так и в фигуральном смысле. Магия может помочь воскреснуть после нелепой гибели, познакомить с прекрасной богиней огня, дать в дар могучее тело демона. Но в этой жизни за все надо платить. И некоторые дары, несмотря на несомненную полезность, бывают отнюдь не безобидны. Увлечение магией в нашем, не приспособленном для колдовства мире, может завести далеко. Очень далеко! Прямо таки в мир иной. Как в буквальном, так и в фигуральном смысле. Магия может помочь воскреснуть после нелепой гибели, познакомить с прекрасной богиней огня, дать в дар могучее тело демона. Но в этой жизни за все надо платить. И некоторые дары, несмотря на несомненную полезность, бывают отнюдь не безобидны."),
        Book("Дорога в маги 2", "Валери я вв", "ссылка", "Жил жлв был герой такой оолы ыооо в ..."),
        Book("Дорога в маги 3", "Валери я вв", "https://cm.author.today/content/2021/10/31/aeb20838d57444ca97ffde2af9856d98.jpg?width=265&height=400&mode=max", "Жил жлв был герой такой оолы ыооо в ..."),
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
        holder.name.text = book.name
        holder.author.text = book.author
        holder.description.text = book.description
        Glide.with(context)
            .load(book.image)
            .placeholder(R.drawable.load)
            .error(R.drawable.ic_launcher_background)
            .into(holder.image)

        holder.view.setOnClickListener{
            val intent = Intent(context, BookActivity::class.java)
            BookActivity.book = book
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listItem.size
    }
}