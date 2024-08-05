package org.daaya.daayalearningapp.exo.video.video_categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.daaya.daayalearningapp.exo.DaayaAndroidApplication
import org.daaya.daayalearningapp.exo.network.DaayaVideoService
import org.daaya.daayalearningapp.exo.network.objects.DaayaVideo
import org.daaya.daayalearningapp.exo.video.videolist.VideoListViewModel
import org.daaya.daayalearningapp.exo.video.videolist.VideoListViewModel.Companion.categorizeVideoList

class VideoCategoriesViewModel : ViewModel() {
    init{
        getAllVideos()
    }
    private val _text = MutableLiveData<String>().apply {
        value = "Categorized Videos"
    }
    val text: LiveData<String> = _text

    private var taxonomySelection = ""
    private val _videoTaxonomyDetails = MutableLiveData<VideoListViewModel.VideoTaxonomyDetails>()
    val videoTaxonomyDetails: LiveData<VideoListViewModel.VideoTaxonomyDetails> = _videoTaxonomyDetails
    private val daayaVideoService: DaayaVideoService =
        DaayaVideoService.Creator.newDaayaVideoService(DaayaAndroidApplication.baseUrl)
    private fun getAllVideos() {
        viewModelScope.launch(Dispatchers.IO) {
            var allVideos = emptyList<DaayaVideo>()
            try {
                allVideos = daayaVideoService.getAllVideos()
                _videoTaxonomyDetails.postValue(categorizeVideoList(allVideos))
            } catch (e: Exception) {
                //do nothing
            }
        }
    }

    private val taxonomyLevel = TaxonomyLevel.CLASS
    private val taxonomyClasses = _videoTaxonomyDetails.value?.taxonomyClasses?.keys?.toList()

    fun getNextTaxonomyLevel (taxonomyLevel: TaxonomyLevel): TaxonomyLevel? {
        return when (taxonomyLevel) {
            TaxonomyLevel.CLASS -> TaxonomyLevel.ORDER
            TaxonomyLevel.ORDER -> TaxonomyLevel.FAMILY
            TaxonomyLevel.FAMILY ->TaxonomyLevel.TRIBE
            TaxonomyLevel.TRIBE -> TaxonomyLevel.GENUS
            else -> null
        }
    }

    fun getCurrentTaxonomyLevel(): TaxonomyLevel = taxonomyLevel

    fun addTaxonomySelection(taxonomy:String) {
        taxonomySelection += taxonomy
        _text.postValue(taxonomySelection)
    }


    enum class TaxonomyLevel  {
        CLASS, ORDER,  FAMILY, TRIBE, GENUS
    }

 }