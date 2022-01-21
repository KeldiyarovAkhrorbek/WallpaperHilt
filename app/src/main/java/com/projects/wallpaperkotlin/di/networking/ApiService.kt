package com.projects.wallpaperkotlin.di.networking

import com.projects.wallpaperkotlin.Keys
import com.projects.wallpaperkotlin.models.PixelsData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {

    @GET("search")
    @Headers("Authorization: ${Keys.key4}")
    suspend fun getSearched(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") size: Int = 30
    ): Response<PixelsData>


    @GET("curated")
    @Headers("Authorization: ${Keys.key4}")
    suspend fun getCurated(
        @Query("page") page: Int,
        @Query("per_page") size: Int = 30
    ): Response<PixelsData>
}