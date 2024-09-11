package org.daaya.daayalearningapp.exo.data

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.daaya.daayalearningapp.exo.data.dao.DaayaVideoDao
import org.daaya.daayalearningapp.exo.data.network.NetworkDataSource
import org.daaya.daayalearningapp.exo.network.objects.DaayaVideo

/**
 * Default implementation of [VideosRepository]. Single entry point for managing tasks' data.
 *
 * @param networkDataSource - The network data source
 * @param localDataSource - The local data source
 * @param dispatcher - The dispatcher to be used for long running or complex operations, such as ID
 * generation or mapping many models.
 * @param scope - The coroutine scope used for deferred jobs where the result isn't important, such
 * as sending data to the network.
 */
@Singleton
class DefaultVideosRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: DaayaVideoDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : VideosRepository {

    override suspend fun getVideos(forceUpdate:Boolean): List<DaayaVideo> {
        if (localDataSource.videos.isEmpty() || forceUpdate){
            refresh()
        }
        return withContext(dispatcher) {
            localDataSource.videos
        }
    }

    override fun getVideosStream(): Flow<List<DaayaVideo>> {
        return localDataSource.observeAll()
            /* .map { videos -> withContext(dispatcher) { videos } } */
    }

    override suspend fun refreshVideo(videoFileName: String) {
        refresh()
    }

    override fun getVideoStream(videoFileName: String): Flow<DaayaVideo> {
        return localDataSource.observeById(videoFileName)//.map { it }
    }

    /**
     * Get a Video with the given ID. Will return null if the task cannot be found.
     *
     * @param videoFileName - The ID of the task
     * @param forceUpdate - true if the task should be updated from the network data source first.
     */
    override suspend fun getVideo(videoFileName: String, forceUpdate: Boolean): DaayaVideo? {
        if (forceUpdate) {
            refresh()
        }
        return localDataSource.getById(videoFileName)
    }

    override suspend fun deleteAllVideos() {
        localDataSource.deleteAll()
        saveTasksToNetwork()
    }

    override suspend fun deleteVideo(videoFileName: String) {
        localDataSource.deleteById(videoFileName)
        saveTasksToNetwork()
    }


    /**
     * The following methods load tasks from (refresh), and save tasks to, the network.
     *
     * Real apps may want to do a proper sync, rather than the "one-way sync everything" approach
     * below. See https://developer.android.com/topic/architecture/data-layer/offline-first
     * for more efficient and robust synchronisation strategies.
     *
     * Note that the refresh operation is a suspend function (forces callers to wait) and the save
     * operation is not. It returns immediately so callers don't have to wait.
     */

    /**
     * Delete everything in the local data source and replace it with everything from the network
     * data source.
     *
     * `withContext` is used here in case the bulk `toLocal` mapping operation is complex.
     */
    override suspend fun refresh() {
        withContext(dispatcher) {
            val videos = networkDataSource.loadVideos()
            localDataSource.deleteAll()
            localDataSource.upsertAll(videos)
        }
    }


    /**
     * Send the tasks from the local data source to the network data source
     *
     * Returns immediately after launching the job. Real apps may want to suspend here until the
     * operation is complete or (better) use WorkManager to schedule this work. Both approaches
     * should provide a mechanism for failures to be communicated back to the user so that
     * they are aware that their data isn't being backed up.
     */
    private fun saveTasksToNetwork() {
        scope.launch {
            try {
                val localTasks = localDataSource.videos
                val networkTasks = withContext(dispatcher) {
                    localTasks
                }
                networkDataSource.saveVideos(networkTasks)
            } catch (e: Exception) {
                // In a real app you'd handle the exception e.g. by exposing a `networkStatus` flow
                // to an app level UI state holder which could then display a Toast message.
            }
        }
    }
}
