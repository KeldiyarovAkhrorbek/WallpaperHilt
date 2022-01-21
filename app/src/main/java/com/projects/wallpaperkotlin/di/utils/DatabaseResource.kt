package com.projects.wallpaperkotlin.di.utils

import com.projects.wallpaperkotlin.entity.PhotoEntity

sealed class DatabaseResource {
    object Loading : DatabaseResource()
    data class Success(val list: List<PhotoEntity>?) : DatabaseResource()
    data class Error(val message: String) : DatabaseResource()
}