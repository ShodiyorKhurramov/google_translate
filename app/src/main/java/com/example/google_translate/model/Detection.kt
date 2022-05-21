package com.example.google_translate.model

data class Detection(
    val confidence: Double,
    val isReliable: Boolean,
    val language: String
)