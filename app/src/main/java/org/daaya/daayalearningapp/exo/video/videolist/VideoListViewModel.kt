package org.daaya.daayalearningapp.exo.video.videolist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.daaya.daayalearningapp.exo.DaayaAndroidApplication
import org.daaya.daayalearningapp.exo.network.DaayaVideoService
import org.daaya.daayalearningapp.exo.network.objects.DaayaVideo
import java.util.concurrent.Executors

class VideoListViewModel : ViewModel() {
    private val _videos = MutableLiveData<List<DaayaVideo>>()
    val videos: LiveData<List<DaayaVideo>> = _videos
    private val daayaVideoService: DaayaVideoService =
        DaayaVideoService.Creator.newDaayaVideoService(DaayaAndroidApplication.baseUrl)
    private var singleThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    fun getAllVideos() {
        viewModelScope.launch(Dispatchers.IO) {
            var allVideos = emptyList<DaayaVideo>()
            try {
                allVideos = daayaVideoService.getAllVideos()
                categorizeVideoList(allVideos)
            } catch (e: Exception) {
                //do nothing
            }
            _videos.postValue(allVideos)
        }
    }


    /* the taxonomy tree follows this hierarchy
          "class"
          "order"
          "family"
          "tribe"
          "genus"
     */
    data class VideoTaxonomyDetails(
        val taxonomyClasses: MutableMap<String, MutableSet<String>>,
        val taxonomyOrders: MutableMap<String, MutableSet<String>>,
        val taxonomyFamilies: MutableMap<String, MutableSet<String>>,
        val taxonomyTribes: MutableMap<String, MutableSet<DaayaVideo>>,
    )

    companion object {
        fun categorizeVideoList(allVideos: List<DaayaVideo>): VideoTaxonomyDetails {
            val taxonomyClasses: MutableMap<String, MutableSet<String>> = mutableMapOf()
            val taxonomyOrders: MutableMap<String, MutableSet<String>> = mutableMapOf()
            val taxonomyFamilies: MutableMap<String, MutableSet<String>> = mutableMapOf()
            val taxonomyTribes: MutableMap<String, MutableSet<DaayaVideo>> = mutableMapOf()
            for (video in allVideos) {
                val taxonomyClas = video.taxonomy.clas
                val taxonomyOrder = video.taxonomy.order
                val taxonomyFamily = video.taxonomy.family
                val taxonomyTribe = video.taxonomy.tribe

                taxonomyClasses.getOrElse(taxonomyClas) {
                    val orders = mutableSetOf<String>()
                    taxonomyClasses[taxonomyClas] = orders
                    orders
                }.add(taxonomyOrder)


                taxonomyOrders.getOrElse(taxonomyOrder) {
                    val families = mutableSetOf<String>()
                    taxonomyOrders[taxonomyOrder] = families
                    families
                }.add(taxonomyFamily)

                taxonomyFamilies.getOrElse(taxonomyFamily) {
                    val tribes = mutableSetOf<String>()
                    taxonomyFamilies[taxonomyFamily] = tribes
                    tribes
                }.add(taxonomyTribe)

                taxonomyTribes.getOrElse(taxonomyTribe) {
                    val genuses = mutableSetOf<DaayaVideo>()
                    taxonomyTribes[taxonomyTribe] = genuses
                    genuses
                }.add(video)

            }
            return VideoTaxonomyDetails(taxonomyClasses, taxonomyOrders, taxonomyFamilies, taxonomyTribes)
        }
    }
}