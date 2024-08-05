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
import org.daaya.daayalearningapp.exo.video.video_categories.VideoCategoryRecyclerViewAdapter.Companion.capitalizeFirstLetter
import org.daaya.daayalearningapp.exo.video.videolist.VideoListViewModel
import org.daaya.daayalearningapp.exo.video.videolist.VideoListViewModel.Companion.categorizeVideoList
import java.util.Stack

class VideoCategoriesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Categorized Videos"
    }
    val text: LiveData<String> = _text

    private var taxonomyLevel = TaxonomyLevel.CLASS
    private var taxonomySelection = ""
    private var taxonomySelectionHierarchy = Stack<String>()
    private lateinit var taxonomyDetails:VideoListViewModel.VideoTaxonomyDetails
    private val _itemList = MutableLiveData<List<String>>()
    val itemList: LiveData<List<String>> = _itemList
    private val daayaVideoService: DaayaVideoService =
        DaayaVideoService.Creator.newDaayaVideoService(DaayaAndroidApplication.baseUrl)
    private fun getAllVideos() {
        viewModelScope.launch(Dispatchers.IO) {
            var allVideos = emptyList<DaayaVideo>()
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



    fun getPrevTaxonomyLevel (): TaxonomyLevel {
        return when (taxonomyLevel) {
            TaxonomyLevel.CLASS -> TaxonomyLevel.CLASS
            TaxonomyLevel.ORDER -> TaxonomyLevel.CLASS
            TaxonomyLevel.FAMILY ->TaxonomyLevel.ORDER
            TaxonomyLevel.TRIBE -> TaxonomyLevel.FAMILY
            TaxonomyLevel.VIDEO -> TaxonomyLevel.TRIBE
            else -> throw RuntimeException()
        }
    }

    fun getNextTaxonomyLevel (): TaxonomyLevel {
        return when (taxonomyLevel) {
            TaxonomyLevel.CLASS -> TaxonomyLevel.ORDER
            TaxonomyLevel.ORDER -> TaxonomyLevel.FAMILY
            TaxonomyLevel.FAMILY ->TaxonomyLevel.TRIBE
            TaxonomyLevel.TRIBE -> TaxonomyLevel.VIDEO
            TaxonomyLevel.VIDEO -> TaxonomyLevel.VIDEO
            else -> throw RuntimeException()
        }
    }

    fun getCurrentTaxonomyLevel(): TaxonomyLevel = taxonomyLevel

    private fun addTaxonomySelection(taxonomy:String) {
        taxonomySelectionHierarchy.add(taxonomy)
        val concated = taxonomySelectionHierarchy.reduce{ acc, item ->
            "$acc/${capitalizeFirstLetter(item)}"
        }
        _text.value = concated
    }

    fun select(item: String):GetListOrString {
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


    enum class TaxonomyLevel  {
        CLASS, ORDER,  FAMILY, TRIBE, GENUS, VIDEO
    }
    init{
        getAllVideos()
    }

 }