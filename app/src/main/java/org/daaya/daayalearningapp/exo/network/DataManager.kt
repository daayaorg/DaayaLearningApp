package org.daaya.daayalearningapp.exo.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.daaya.daayalearningapp.exo.network.objects.DaayaVideo
import timber.log.Timber
import java.util.concurrent.Executors


class DataManager : ViewModel() {
    private val daayaVideoService: DaayaVideoService =
        DaayaVideoService.Creator.newDaayaVideoService(
            baseUrl
        )

    companion object {
        var singleThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        var baseUrl: String = "http://48.217.169.49:8182/"
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
