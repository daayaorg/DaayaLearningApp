package org.daaya.daayalearningapp.exo.data.network
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.daaya.daayalearningapp.exo.DaayaAndroidApplication
import org.daaya.daayalearningapp.exo.network.DaayaVideoService
import org.daaya.daayalearningapp.exo.network.objects.DaayaVideo
import javax.inject.Inject

class VideoNetworkDataSource @Inject constructor() : NetworkDataSource {
    // A mutex is used to ensure that reads and writes are thread-safe.
    private val accessMutex = Mutex()
    private var allVideos = emptyList<DaayaVideo>()

    override suspend fun loadVideos(): List<DaayaVideo> = accessMutex.withLock {
        val daayaVideoService = DaayaVideoService.Creator.newDaayaVideoService(DaayaAndroidApplication.baseUrl)
        allVideos = daayaVideoService.getAllVideos()
        return allVideos
    }

    override suspend fun saveVideos(videos: List<DaayaVideo>) = accessMutex.withLock {
        //TODO: if we do video upload then this is the place
    }
}