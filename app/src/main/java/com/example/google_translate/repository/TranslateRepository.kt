package com.example.google_translate.repository

import com.example.google_translate.data.database.dao.NoteDao
import com.example.google_translate.data.database.entity.Note
import com.example.google_translate.data.api.ApiService
import com.example.google_translate.model.Detection

class TranslateRepository(
    private val noteDao: NoteDao,
    private val apiService: ApiService
) {
    suspend fun translations(word: String, target: String, source: String) = apiService.translations(word, target, source)

    suspend fun detection(detection: String) = apiService.detections(detection)

    suspend fun addNote(note: Note) = noteDao.addNote(note)

    suspend fun getAllNotes() = noteDao.getAllNotes()

    suspend fun removeNote(noteId: Int) = noteDao.removeNote(noteId)

}