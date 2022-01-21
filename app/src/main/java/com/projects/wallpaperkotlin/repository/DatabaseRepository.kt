package com.projects.wallpaperkotlin.repository

import com.projects.wallpaperkotlin.dao.PhotoDao
import com.projects.wallpaperkotlin.entity.PhotoEntity
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val photoDao: PhotoDao) {
    suspend fun addPhoto(photoEntity: PhotoEntity) = photoDao.addPhoto(photoEntity)
    suspend fun deletePhoto(photoEntity: PhotoEntity) = photoDao.deletePhoto(photoEntity)
    suspend fun getAllPhotos() = flow { emit(photoDao.getAllPhotos()) }
    suspend fun isAvailable(url: String) = flow { emit(photoDao.isAvailable(url)) }
    suspend fun getPhotoByUrl(url: String) = flow { emit(photoDao.getPhotoByUrl(url)) }
}