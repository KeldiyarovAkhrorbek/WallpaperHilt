package com.projects.wallpaperkotlin.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.projects.wallpaperkotlin.dao.PhotoDao
import com.projects.wallpaperkotlin.entity.PhotoEntity

@Database(
    entities = [PhotoEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun photoDao(): PhotoDao
}