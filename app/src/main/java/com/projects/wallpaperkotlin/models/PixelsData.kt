package com.projects.wallpaperkotlin.models

data class PixelsData(
    val next_page: String,
    val page: Int,
    val per_page: Int,
    val photos: List<Photo>,
    val total_results: Int
)