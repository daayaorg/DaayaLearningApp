package org.daaya.daayalearningapp.exo.video.videolist

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.daaya.daayalearningapp.exo.network.objects.DaayaVideo
import java.lang.reflect.Type
import java.util.Scanner


class VideoListViewModelTest {

    @org.junit.Before
    fun setUp() {
    }

    @org.junit.After
    fun tearDown() {
    }

    @org.junit.Test
    fun categorizeVideoList() {
        val fooType: Type = object : TypeToken<List<DaayaVideo>>() {}.type
        val inputStream =  javaClass.getResourceAsStream("/allVideos.json")
        //val result1 = BufferedReader(InputStreamReader(inputStream))
            //.lines().collect(Collectors.joining("\n"))
        val s = Scanner(inputStream).useDelimiter("\\A")
        val result = if (s.hasNext()) s.next() else ""
        val allVideos = Gson().fromJson<List<DaayaVideo>>(result, fooType)
        val videoTaxonomyDetails = VideoListViewModel.categorizeVideoList(allVideos)
        assert(videoTaxonomyDetails.taxonomyClasses.contains("elementary"))
        assert(videoTaxonomyDetails.taxonomyClasses["elementary"]!!.contains("math"))
        assert(videoTaxonomyDetails.taxonomyClasses["elementary"]!!.contains("language"))
        assert(videoTaxonomyDetails.taxonomyOrders.contains("math"))
        assert(videoTaxonomyDetails.taxonomyOrders.contains("language"))
        assert(videoTaxonomyDetails.taxonomyOrders["language"]!!.contains("Alphabets"))
        assert(videoTaxonomyDetails.taxonomyOrders["math"]!!.contains("number system"))
    }
}