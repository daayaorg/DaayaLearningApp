package org.daaya.daayalearningapp.exo.ui.video.video_categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.daaya.daayalearningapp.exo.DaayaAndroidApplication
import org.daaya.daayalearningapp.exo.data.VideosRepository
import org.daaya.daayalearningapp.exo.network.DaayaVideoService
import org.daaya.daayalearningapp.exo.network.objects.DaayaVideo
import org.daaya.daayalearningapp.exo.ui.video.video_categories.VideoCategoryRecyclerViewAdapter.Companion.capitalizeFirstLetter
import org.daaya.daayalearningapp.exo.ui.video.videolist.VideoListViewModel
import org.daaya.daayalearningapp.exo.ui.video.videolist.VideoListViewModel.Companion.categorizeVideoList
import timber.log.Timber
import java.util.Stack
import javax.inject.Inject

class VideoCategoriesViewModel @Inject constructor(
    private val repository: VideosRepository,
    savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _itemList = MutableLiveData<List<String>>()
    val itemList: LiveData<List<String>> = _itemList
    private val _text = MutableLiveData<String>().apply {
        value = "Choose one"
    }
    val text: LiveData<String> = _text


    private val _progressBarVisibility = MutableLiveData(true)
    val progressBarVisibility = _progressBarVisibility

    private var taxonomyLevel = TaxonomyLevel.CLASS
    private var taxonomySelection = ""
    private var taxonomySelectionHierarchy = Stack<String>()
    private lateinit var taxonomyDetails: VideoListViewModel.VideoTaxonomyDetails
    private fun getAllVideos() {
        val daayaVideoService = DaayaVideoService.Creator.newDaayaVideoService(DaayaAndroidApplication.baseUrl)
        viewModelScope.launch(Dispatchers.IO) {
            val allVideos: List<DaayaVideo>
            try {
                allVideos = daayaVideoService.getAllVideos()
                taxonomyDetails = categorizeVideoList(allVideos)
                val listOrString = getList()
                if (listOrString.isList) {
                    _itemList.postValue(listOrString.list)
                }
            } catch (e: Exception) {
                //do nothing
            }
        }
    }



    private fun getPrevTaxonomyLevel (): TaxonomyLevel {
        return when (taxonomyLevel) {
            TaxonomyLevel.CLASS -> TaxonomyLevel.CLASS
            TaxonomyLevel.ORDER -> TaxonomyLevel.CLASS
            TaxonomyLevel.FAMILY -> TaxonomyLevel.ORDER
            TaxonomyLevel.TRIBE -> TaxonomyLevel.FAMILY
            TaxonomyLevel.VIDEO -> TaxonomyLevel.TRIBE
            else -> throw RuntimeException()
        }
    }

    private fun getNextTaxonomyLevel (): TaxonomyLevel {
        return when (taxonomyLevel) {
            TaxonomyLevel.CLASS -> TaxonomyLevel.ORDER
            TaxonomyLevel.ORDER -> TaxonomyLevel.FAMILY
            TaxonomyLevel.FAMILY -> TaxonomyLevel.TRIBE
            TaxonomyLevel.TRIBE -> TaxonomyLevel.VIDEO
            TaxonomyLevel.VIDEO -> TaxonomyLevel.VIDEO
            else -> throw RuntimeException()
        }
    }

    private fun addTaxonomySelection(taxonomy:String) {
        taxonomySelectionHierarchy.add(taxonomy)
        val concated = taxonomySelectionHierarchy.map{ capitalizeFirstLetter(it) }
                                                 .reduce { acc, item -> "$acc/${item}" }
        _text.value = concated
    }

    fun select(item: String): GetListOrString {
        addTaxonomySelection(item)
        taxonomySelection = item
        taxonomyLevel = getNextTaxonomyLevel()
        val listOrString = getList()
        if (listOrString.isList) {
            _itemList.value = listOrString.list
        }
        return listOrString
    }

    class GetListOrString {
        constructor(list: List<String>) {
            this.list = list
            isList = true
        }

        constructor(str: String?) {
            str?.let { this.str = str }?: { this.str = "" }
        }

        var isList = false
        lateinit var str :String
        lateinit var list:List<String>
    }


    private fun getList(): GetListOrString { //List<String>{
        when (taxonomyLevel) {
            TaxonomyLevel.CLASS -> return GetListOrString(taxonomyDetails.taxonomyClasses.keys.toList())
            TaxonomyLevel.ORDER -> return GetListOrString(taxonomyDetails.taxonomyClasses[taxonomySelection]!!.toList())
            TaxonomyLevel.FAMILY -> return GetListOrString(taxonomyDetails.taxonomyOrders[taxonomySelection]!!.toList())
            TaxonomyLevel.TRIBE -> return GetListOrString(taxonomyDetails.taxonomyFamilies[taxonomySelection]!!.toList())
                /*
                {
                val videoList = taxonomyDetails.taxonomyTribes[taxonomySelection]
                videoList?.let { it ->
                    it.map { (it.title) }
                }?: emptyList()
            }

                 */
            TaxonomyLevel.VIDEO -> {
                val filename = taxonomyDetails.taxonomyTribes[taxonomySelection]
                return GetListOrString(filename?.toList()?.get(0)?.filename)
            }
            else -> return GetListOrString(emptyList())
        }
    }

    fun handleBackPressed(): Boolean {
        if (taxonomySelectionHierarchy.isEmpty()){
            return  false
        }
        taxonomySelectionHierarchy.pop()
        taxonomyLevel = getPrevTaxonomyLevel()

        taxonomySelection = if (taxonomySelectionHierarchy.isEmpty()) ""
        else taxonomySelectionHierarchy.peek()

        val listOrString = getList()
        if (listOrString.isList) {
            _itemList.value = listOrString.list
            val concated = if (taxonomySelectionHierarchy.isEmpty()) ""
            else taxonomySelectionHierarchy.reduce { acc, item ->
                "$acc/${capitalizeFirstLetter(item)}"
            }
            _text.value = concated
        }
        return true
    }

    fun getVideo(): DaayaVideo? {
        if (taxonomyLevel == TaxonomyLevel.VIDEO) {
            val filename = taxonomyDetails.taxonomyTribes[taxonomySelection]
            return filename?.toList()?.get(0)
        } else {
            return null
        }
    }

    fun refresh(forceUpdate:Boolean = false) {
        progressBarVisibility.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val allVideos: List<DaayaVideo>
            try {
                allVideos = repository.getVideos(forceUpdate)
                taxonomyDetails = categorizeVideoList(allVideos)
                val listOrString = getList()
                if (listOrString.isList) {
                    _itemList.postValue(listOrString.list)
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        progressBarVisibility.value = false
        //getAllVideos()
    }


    private enum class TaxonomyLevel  {
        CLASS, ORDER,  FAMILY, TRIBE, GENUS, VIDEO
    }
    init{
        refresh()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY])
                // Create a SavedStateHandle for this ViewModel from extras
                val savedStateHandle = extras.createSavedStateHandle()

                return VideoCategoriesViewModel(
                    (application as DaayaAndroidApplication).videosRepository,
                    savedStateHandle
                ) as T
            }
        }
    }


}