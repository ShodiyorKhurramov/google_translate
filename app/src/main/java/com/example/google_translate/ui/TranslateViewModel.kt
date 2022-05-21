package com.example.google_translate.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.google_translate.data.database.entity.Note
import com.example.google_translate.model.*
import com.example.google_translate.repository.TranslateRepository
import com.example.google_translate.utils.UiStateList
import com.example.google_translate.utils.UiStateObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class TranslateViewModel(private val translaterepository: TranslateRepository) : ViewModel() {

    private val _translationState =
        MutableStateFlow<UiStateObject<Response<TranslationData<Translation>>>>(UiStateObject.EMPTY)
    val translationState = _translationState

    fun getTranslation(word: String, target: String, source: String) = viewModelScope.launch {
        _translationState.value = UiStateObject.LOADING
        try {
            val translations = translaterepository.translations(word, target, source)
            _translationState.value = UiStateObject.SUCCESS(translations)
        } catch (e: Exception) {
            _translationState.value = UiStateObject.ERROR(e.localizedMessage ?: "No connection")

        }
    }

    private val _detectionState =
        MutableStateFlow<UiStateObject<Response<DetectionData<Detection>>>>(UiStateObject.EMPTY)
    val detectionState = _detectionState

    fun getDetection(detection: String) = viewModelScope.launch {
        _detectionState.value = UiStateObject.LOADING
        try {
            val detections = translaterepository.detection(detection)
            _detectionState.value = UiStateObject.SUCCESS(detections)
        } catch (e: Exception) {
            _detectionState.value = UiStateObject.ERROR(e.localizedMessage ?: "No connection")

        }
    }

    private val _getAllNotesState = MutableStateFlow<UiStateList<Note>>(UiStateList.EMPTY)
    val getAllNotesState = _getAllNotesState

    fun getAllNotes() = viewModelScope.launch {
        _getAllNotesState.value = UiStateList.LOADING
        try {
            val getAllnotes = translaterepository.getAllNotes()
            _getAllNotesState.value = UiStateList.SUCCESS(getAllnotes)
        } catch (e: Exception) {
            _getAllNotesState.value = UiStateList.ERROR(e.localizedMessage ?: "No connection")

        }
    }

    fun addNote(note: Note) = viewModelScope.launch {
        translaterepository.addNote(note)
    }


    fun removeNote(id : Int) = viewModelScope.launch {
        translaterepository.removeNote(id)
    }




}