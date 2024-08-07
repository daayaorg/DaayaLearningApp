package org.daaya.daayalearningapp.exo.ui.video.videolist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.daaya.daayalearningapp.exo.DaayaAndroidApplication
import org.daaya.daayalearningapp.exo.network.DaayaVideoService
import org.daaya.daayalearningapp.exo.network.objects.DaayaVideo
import org.daaya.daayalearningapp.exo.ui.video.video_categories.VideoCategoriesViewModel.GetListOrString
import org.daaya.daayalearningapp.exo.ui.video.video_categories.VideoCategoryRecyclerViewAdapter.Companion.capitalizeFirstLetter
import java.util.Stack

class VideoListViewModel : ViewModel() {
    private val _itemList = MutableLiveData<List<String>>()
    val itemList: LiveData<List<String>> = _itemList

    private val _text = MutableLiveData<String>().apply {
        value = ""
    }
    val text: LiveData<String> = _text

    private var taxonomySelection = ""
    private val daayaVideoService = DaayaVideoService.Creator.newDaayaVideoService(DaayaAndroidApplication.baseUrl)
    private var taxonomySelectionHierarchy = Stack<String>()
    //private var singleThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private lateinit var taxonomyDetails: VideoTaxonomyDetails
    private var taxonomyLevel = TaxonomyLevel.AUTHOR
    private fun getAllVideos() {
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

    private fun getNextTaxonomyLevel (): TaxonomyLevel {
        return when (taxonomyLevel) {
            TaxonomyLevel.AUTHOR -> TaxonomyLevel.VIDEOS
            TaxonomyLevel.VIDEOS -> TaxonomyLevel.VIDEO
            TaxonomyLevel.VIDEO -> TaxonomyLevel.VIDEO
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


    private fun getPrevTaxonomyLevel (): TaxonomyLevel {
        return when (taxonomyLevel) {
            TaxonomyLevel.AUTHOR -> TaxonomyLevel.AUTHOR
            TaxonomyLevel.VIDEOS -> TaxonomyLevel.AUTHOR
            TaxonomyLevel.VIDEO -> TaxonomyLevel.VIDEOS
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
        return if (taxonomyLevel == TaxonomyLevel.VIDEO) {
            taxonomyDetails.videos[taxonomySelection]
        } else {
            null
        }
    }

    private fun getList(): GetListOrString { //List<String>{
        when (taxonomyLevel) {
            TaxonomyLevel.AUTHOR -> return GetListOrString(taxonomyDetails.authors.keys.toList())
            TaxonomyLevel.VIDEOS -> {
                val videosByAuthor = taxonomyDetails.authors[taxonomySelection]
                val videoList = videosByAuthor?.let {
                    videosByAuthor.map { it.title }
                }?: emptyList()
                return GetListOrString(videoList)
            }
            TaxonomyLevel.VIDEO -> return GetListOrString(taxonomyDetails.videos[taxonomySelection]?.filename)
            else -> return GetListOrString(emptyList())
        }
    }

    fun hideIcons():Boolean {
        return when (taxonomyLevel) {
            TaxonomyLevel.AUTHOR -> false
            TaxonomyLevel.VIDEOS -> true
            TaxonomyLevel.VIDEO -> true
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
        val videos: MutableMap<String, DaayaVideo>,
        val authors: MutableMap<String, MutableSet<DaayaVideo>>,
    )

    companion object {
        fun categorizeVideoList(allVideos: List<DaayaVideo>): VideoTaxonomyDetails {
            val taxonomyClasses: MutableMap<String, MutableSet<String>> = mutableMapOf()
            val taxonomyOrders: MutableMap<String, MutableSet<String>> = mutableMapOf()
            val taxonomyFamilies: MutableMap<String, MutableSet<String>> = mutableMapOf()
            val taxonomyTribes: MutableMap<String, MutableSet<DaayaVideo>> = mutableMapOf()
            val videos: MutableMap<String, DaayaVideo> = mutableMapOf()
            val authors: MutableMap<String, MutableSet<DaayaVideo>> = mutableMapOf()
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


                authors.getOrElse(video.author) {
                    val videosByAuthor = mutableSetOf<DaayaVideo>()
                    authors[video.author] = videosByAuthor
                    videosByAuthor
                }.add(video)

                videos[video.title] = video

            }
            return VideoTaxonomyDetails(taxonomyClasses, taxonomyOrders, taxonomyFamilies, taxonomyTribes, videos, authors)
        }
    }

    init{
        getAllVideos()
    }

    private enum class TaxonomyLevel  {
        AUTHOR, VIDEOS, VIDEO
    }
}