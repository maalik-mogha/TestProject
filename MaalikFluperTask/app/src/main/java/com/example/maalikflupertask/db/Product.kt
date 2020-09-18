package com.example.maalikflupertask.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Product(
    val name: String,
    val detail: String,
    val price: String,
    val image: String

):Serializable{
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}

