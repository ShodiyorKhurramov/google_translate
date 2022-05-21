package com.example.google_translate.data.api

import com.example.google_translate.model.*
import retrofit2.http.*

interface ApiService {

    @POST("language/translate/v2")
    @FormUrlEncoded
    suspend fun translations(
        @Field("q") word: String,
        @Field("target") target: String,
        @Field("source") source: String
    ): Response<TranslationData<Translation>>

    @POST("language/translate/v2/detect")
    @FormUrlEncoded
    suspend fun detections(@Field("q") detection: String): Response<DetectionData<Detection>>
}