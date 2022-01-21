package com.projects.wallpaperkotlin.repository


import com.projects.wallpaperkotlin.di.networking.ApiService
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PhotoRepository @Inject constructor(private val apiService: ApiService) {

    fun getSearchedPhotos(page: Int, query: String) =
        flow { emit(apiService.getSearched(query = query, page = page)) }


    fun getCuratedPhotos(page: Int) =
        flow { emit(apiService.getCurated(page = page)) }
}