package ru.lytvest.audiobooks

import android.os.Parcel
import android.os.Parcelable


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
) : Parcelable {
    val firstLine = "$circle $number $name"

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt() ,
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(circle)
        parcel.writeInt(number)
        parcel.writeString(reader)
        parcel.writeString(author)
        parcel.writeString(image)
        parcel.writeString(description)
        parcel.writeString(torrent)
        parcel.writeString(magnet)
        parcel.writeDouble(rating)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel)
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }
}
