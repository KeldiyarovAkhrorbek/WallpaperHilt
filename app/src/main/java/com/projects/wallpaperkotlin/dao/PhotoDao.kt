package com.projects.wallpaperkotlin.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.projects.wallpaperkotlin.entity.PhotoEntity

@Dao
interface PhotoDao {

    @Insert(onConflict = REPLACE)
    suspend fun addPhoto(photoEntity: PhotoEntity)

    @Delete
    suspend fun deletePhoto(photoEntity: PhotoEntity)

    @Query("select * from PhotoEntity")
    suspend fun getAllPhotos(): List<PhotoEntity>

    @Query("SELECT EXISTS(SELECT * FROM PhotoEntity WHERE photoUrl = :url)")
    suspend fun isAvailable(url: String): Boolean

    @Query("select * from PhotoEntity where photoUrl = :url")
    suspend fun getPhotoByUrl(url: String): PhotoEntity?


}