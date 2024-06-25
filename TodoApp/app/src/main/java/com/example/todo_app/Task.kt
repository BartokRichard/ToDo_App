package com.example.todo_app

import android.os.Parcel
import android.os.Parcelable

data class Task(
    val title: String,
    val date: Long?,
    val time: Long?,
    val category: String,
    var isCompleted: Boolean,
    val categoryIcon: Int // Tárolja az ikon erőforrás ID-ját
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeValue(date)
        parcel.writeValue(time)
        parcel.writeString(category)
        parcel.writeByte(if (isCompleted) 1 else 0)
        parcel.writeInt(categoryIcon)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
