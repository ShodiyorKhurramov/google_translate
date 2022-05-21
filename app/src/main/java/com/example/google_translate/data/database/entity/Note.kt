package com.example.google_translate.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tn_note")
data class Note (
    @PrimaryKey(autoGenerate = true)
    val id: Int?=null,
    val uz: String,
    val en: String,

)