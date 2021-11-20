package ru.lytvest.audiobooks

import java.io.Serializable

@kotlinx.serialization.Serializable
data class Book(
    val name:String,
    val circle: String = "",
    val number: Int = 1,
    val reader: String = "",
    val author:String = "",
    val image:String = "",
    val description: String = "",
    val torrent: String = "",
    val magnet: String = "",
    val rating: Double = 0.0
) {
    val firstLine = "$circle $number $name"
}
