package com.projects.wallpaperkotlin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.projects.wallpaperkotlin.di.utils.DatabaseExistsResource
import com.projects.wallpaperkotlin.di.utils.DatabaseResource
import com.projects.wallpaperkotlin.di.utils.DatabaseSingleResource
import com.projects.wallpaperkotlin.entity.PhotoEntity
import com.projects.wallpaperkotlin.repository.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DatabaseViewModel @Inject constructor(private val databaseRepository: DatabaseRepository) :
    ViewModel() {

    fun addPhoto(photoEntity: PhotoEntity) {
        viewModelScope.launch {
            databaseRepository.addPhoto(photoEntity)
        }
    }

    fun deletePhoto(photoEntity: PhotoEntity) {
        viewModelScope.launch {
            databaseRepository.deletePhoto(photoEntity)
        }
    }

    fun getAllPhotos(): StateFlow<DatabaseResource> {
        val stateFlow = MutableStateFlow<DatabaseResource>(DatabaseResource.Loading)
        viewModelScope.launch {
            val flow = databaseRepository.getAllPhotos()
            flow.catch {
                stateFlow.emit(DatabaseResource.Error(it.message ?: ""))
            }.collect {
                stateFlow.emit(DatabaseResource.Success(it))
            }
        }
        return stateFlow
    }

    fun isAvailable(url: String): StateFlow<DatabaseExistsResource> {
        val stateFlow = MutableStateFlow<DatabaseExistsResource>(DatabaseExistsResource.Loading)
        viewModelScope.launch {
            val flow = databaseRepository.isAvailable(url)
            flow.catch {
                stateFlow.emit(DatabaseExistsResource.Error(it.message ?: ""))
            }.collect {
                stateFlow.emit(DatabaseExistsResource.Success(it))
            }
        }
        return stateFlow
    }

    fun getPhotoByUrl(url: String): StateFlow<DatabaseSingleResource> {
        val stateFlow = MutableStateFlow<DatabaseSingleResource>(DatabaseSingleResource.Loading)
        viewModelScope.launch {
            val flow = databaseRepository.getPhotoByUrl(url)
            flow.catch {
                stateFlow.emit(DatabaseSingleResource.Error(it.message ?: ""))
            }.collect {
                stateFlow.emit(DatabaseSingleResource.Success(it))
            }
        }
        return stateFlow
    }
}