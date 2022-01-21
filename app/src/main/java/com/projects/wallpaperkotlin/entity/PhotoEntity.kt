package com.projects.wallpaperkotlin.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class PhotoEntity(
    @PrimaryKey
    val id: Int,
    val photographer_url: String,
    val height: Int,
    val width: Int,
    val photographer: String,
    val photoUrl: String,
    var liked: Boolean
) : Serializable
