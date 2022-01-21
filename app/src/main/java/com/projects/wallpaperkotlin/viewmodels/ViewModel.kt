package com.projects.wallpaperkotlin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.projects.wallpaperkotlin.repository.PhotoDataSourceCurated
import com.projects.wallpaperkotlin.repository.PhotoDataSourceSearch
import com.projects.wallpaperkotlin.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor(private val photoRepository: PhotoRepository) :
    ViewModel() {

    fun loadSearched(query: String) =
        Pager(PagingConfig(100)) {
            PhotoDataSourceSearch(query, photoRepository)
        }.flow.cachedIn(viewModelScope)


    fun loadCurated() =
        Pager(PagingConfig(100)) {
            PhotoDataSourceCurated(photoRepository)
        }.flow.cachedIn(viewModelScope)

//    fun getUsers(): StateFlow<UserResource> {
//        val stateFlow = MutableStateFlow<UserResource>(UserResource.Loading)
//        viewModelScope.launch {
//            userRepository.getUsers()
//                .catch {
//                    stateFlow.emit(UserResource.Error(it.message ?: ""))
//                }.collect {
//                    if (it.isSuccessful) {
//                        stateFlow.emit(UserResource.Success(it.body()))
//                    }
//                }
//        }
//        return stateFlow
//    }


}