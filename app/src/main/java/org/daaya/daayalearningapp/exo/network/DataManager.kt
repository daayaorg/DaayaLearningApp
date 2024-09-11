package org.daaya.daayalearningapp.exo.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.daaya.daayalearningapp.exo.DaayaAndroidApplication.baseUrl
import timber.log.Timber
import java.util.concurrent.Executors


class DataManager : ViewModel() {
    //TODO: use inject here
    private val daayaVideoService: DaayaVideoService =
        DaayaVideoService.Creator.newDaayaVideoService(
            baseUrl
        )

    companion object {
        var singleThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    }
    fun getAllVideos() {
        viewModelScope.launch(singleThreadDispatcher) {
           val videos = daayaVideoService.getAllVideos()
            Timber.e("videos=$videos")
        }
    }

    private fun launchTaskOnBackground(task: () -> Unit) {
        viewModelScope.launch(singleThreadDispatcher) {
            task.invoke()
        }
    }
}
