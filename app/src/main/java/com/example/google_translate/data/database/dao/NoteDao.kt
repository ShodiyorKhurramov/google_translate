package com.example.google_translate.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.google_translate.data.database.entity.Note

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(note: Note)

    @Query("select * from tn_note")
    suspend fun getAllNotes(): List<Note>

    @Query("DELETE FROM tn_note WHERE id=:noteId")
    suspend fun removeNote(noteId: Int)
}