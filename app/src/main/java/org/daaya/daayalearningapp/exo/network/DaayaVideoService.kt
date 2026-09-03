package org.daaya.daayalearningapp.exo.network

import android.util.Log
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.daaya.daayalearningapp.exo.BuildConfig
import org.daaya.daayalearningapp.exo.network.objects.DaayaVideo
import org.joda.time.DateTimeZone
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import timber.log.Timber
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

interface DaayaVideoService {
    @GET("api/v1/videos")
    suspend fun  getAllVideos(): List<DaayaVideo>

    @GET("api/v1/videos")
    fun getVideosByClassification(
        @Query("rank") rankName: String?,
        @Query("value") rankValue: String?
    ): List<DaayaVideo?>?

    /******** Helper class that sets up a new services  */
    object Creator {
        fun newDaayaVideoService(baseUrlIn: String): DaayaVideoService {
            Timber.i("baseUrl = %s", baseUrlIn)
            var baseUrl = baseUrlIn.trim { it <= ' ' }
            while (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length - 1)
            }

            if (!baseUrl.endsWith("/")) {
                baseUrl = "$baseUrl/"
            }

            val gson = GsonBuilder()
                .registerTypeAdapter(
                    DateTimeZone::class.java,
                    JsonDeserializer { jsonElement: JsonElement, _: Type?, _: JsonDeserializationContext? ->
                        DateTimeZone.forID(jsonElement.asString)
                    } as JsonDeserializer<DateTimeZone>)
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create()


            val logger =
                HttpLoggingInterceptor.Logger { message: String? -> Log.i("OkHttp", message!!) }
            val logging = HttpLoggingInterceptor(logger)
            if (BuildConfig.DEBUG) logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            else logging.setLevel(HttpLoggingInterceptor.Level.NONE)

            val client = OkHttpClient.Builder()
                .readTimeout(TIMEOUT_SECS.toLong(), TimeUnit.SECONDS)
                .followRedirects(true)
                .followSslRedirects(true)
                .connectTimeout(TIMEOUT_SECS.toLong(), TimeUnit.SECONDS)
                .addInterceptor(AppOkHttpInterceptor())
                .addInterceptor(logging)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build()
            return retrofit.create(DaayaVideoService::class.java)
        }
    }

    companion object {
        const val TIMEOUT_SECS: Int = 60
    }
}
