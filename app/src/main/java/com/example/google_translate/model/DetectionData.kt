package com.example.google_translate.model

data class DetectionData<T>(
    val detections: List<List<T>>
)