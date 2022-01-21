package com.projects.wallpaperkotlin.di.utils

import com.projects.wallpaperkotlin.models.Photo

sealed class PhotoResource {
    object Loading : PhotoResource()
    data class Success(val list: List<Photo>?) : PhotoResource()
    data class Error(val message: String) : PhotoResource()
}