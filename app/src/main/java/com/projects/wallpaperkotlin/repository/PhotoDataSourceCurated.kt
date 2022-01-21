package com.projects.wallpaperkotlin.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.projects.wallpaperkotlin.models.Photo
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class PhotoDataSourceCurated @Inject constructor(
    private val photoRepository: PhotoRepository
) :
    PagingSource<Int, Photo>() {


    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        try {
            val currentPage = params.key ?: 1

            var loadResult: LoadResult.Page<Int, Photo>? = null

            if (params.key ?: 1 >= 1) {
                photoRepository.getCuratedPhotos(currentPage)
                    .catch {
                        loadResult = LoadResult.Page(
                            emptyList(),
                            currentPage - 1, currentPage + 1
                        )
                    }.collect {
                        loadResult =
                            LoadResult.Page(
                                it.body()?.photos!!,
                                currentPage - 1, currentPage + 1
                            )
                    }
            } else {
                loadResult =
                    LoadResult.Page(
                        emptyList(),
                        null, currentPage + 1
                    )
            }
            return loadResult!!
        } catch (e: Exception) {
            return LoadResult.Error(e.fillInStackTrace())
        }
    }
}