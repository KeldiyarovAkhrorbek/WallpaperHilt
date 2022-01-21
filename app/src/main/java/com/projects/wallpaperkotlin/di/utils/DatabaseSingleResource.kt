package com.projects.wallpaperkotlin.di.utils

import com.projects.wallpaperkotlin.entity.PhotoEntity

sealed class DatabaseSingleResource {
    object Loading : DatabaseSingleResource()
    data class Success(val photoEntity: PhotoEntity?) : DatabaseSingleResource()
    data class Error(val message: String) : DatabaseSingleResource()
}