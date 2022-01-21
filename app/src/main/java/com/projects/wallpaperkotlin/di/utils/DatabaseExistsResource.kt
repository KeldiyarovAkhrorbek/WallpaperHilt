package com.projects.wallpaperkotlin.di.utils

sealed class DatabaseExistsResource {
    object Loading : DatabaseExistsResource()
    data class Success(val exists: Boolean) : DatabaseExistsResource()
    data class Error(val message: String) : DatabaseExistsResource()
}