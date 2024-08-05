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


    /*
    @GET("api/v1/Organizations/{tenantId}/Locations/{locationId}/Contacts")
    Observable<List<Contact>> getContactsForLocation(@Header("Cookie") String cookie,
                                                     @Path  ("tenantId") String tenantId,
                                                     @Path  ("locationId") String locationId);

    @GET("api/v1/Organizations/{tenantId}/Cameras/{cameraId}")
    Observable<Camera> fetchCamera(@Header ("Cookie") String cookie,
                                    @Path  ("tenantId") String tenantId,
                                    @Path  ("cameraId")String cameraId);

    @PUT("api/v1/Organizations/{tenantId}/Cameras/{cameraId}/Arm")
    Observable<Response<ResponseBody>> armCamera(@Header("Cookie") String cookie,
                                                 @Path  ("tenantId") String tenantId,
                                                 @Path  ("cameraId")String cameraId);

    @PUT("api/v1/Organizations/{tenantId}/Cameras/{cameraId}/Disarm")
    Observable<Response<ResponseBody>> disarmCamera(@Header("Cookie") String cookie,
                                                    @Path  ("tenantId") String tenantId,
                                                    @Path  ("cameraId")String cameraId,
                                                    @Body DisarmOptions disarmOptions);

    @POST("api/v1/Organizations/{tenantId}/Devices/{deviceId}/Gateway/Reboot")
    Observable<Response<ResponseBody>> rebootDevice(@Header("Cookie") String cookie,
                                                    @Path  ("tenantId") String tenantId,
                                                    @Path  ("deviceId")String deviceId);

    @PUT("api/v1/Organizations/{tenantId}/Devices/{deviceId}/Gateway/poe")
    Observable<Response<ResponseBody>> sendPoeSettings(@Header("Cookie") String cookie,
                                         @Path  ("tenantId") String tenantId,
                                         @Path  ("deviceId")String deviceId,
                                         @Body  PoePorts poePorts);

    @GET("api/v1/Organizations/{tenantId}/Devices/{deviceId}/Gateway/poe")
    Observable<PoePorts> fetchPoeSettings(@Header("Cookie") String cookie,
                                          @Path  ("tenantId") String tenantId,
                                          @Path  ("deviceId")String deviceId);

    @GET("api/v1/Organizations/{tenantId}/Devices/{deviceId}/Gateway/entities")
    Observable<GatewayEntityWrapper> fetchGatewayEntities(@Header("Cookie") String cookie,
                                                          @Path  ("tenantId") String tenantId,
                                                          @Path  ("deviceId")String deviceId);

    @GET("api/v1/Organizations/{tenantId}/Devices/{deviceId}/Gateway/entity-settings")
    Observable<DigitalOutputEntitySettings> fetchGatewayEntitySettings(@Header("Cookie") String cookie,
                                                                       @Path  ("tenantId") String tenantId,
                                                                       @Path  ("deviceId")String deviceId,
                                                                       @Query("parameters") String settingParams);

    @PUT("api/v1/Organizations/{tenantId}/Devices/{deviceId}/Gateway/camera-trigger-digital-output")
    Observable<Response<ResponseBody>> triggerDigitalOutput(@Header("Cookie") String cookie,
                                                            @Path  ("tenantId") String tenantId,
                                                            @Path  ("deviceId")String deviceId,
                                                            @Body TriggerDigitalOut settings);

    @GET("api/v1/Organizations/{tenantId}/Devices/{deviceId}/Gateway/cameras")
    Observable<Response<ResponseBody>> fetchPoeCameras(@Header("Cookie") String cookie,
                                                       @Path  ("tenantId") String tenantId,
                                                       @Path  ("deviceId")String deviceId);

    @GET("api/v1/Organizations/{tenantId}/Devices/{deviceId}/Gateway/switch")
    Observable<PoeSwitches> fetchPoeSwitchSettings(@Header("Cookie") String cookie,
                                                   @Path  ("tenantId") String tenantId,
                                                   @Path  ("deviceId")String deviceId);

    @PUT("api/v1/Organizations/{tenantId}/Devices/{deviceId}/Gateway/camera-tour-stop")
    Observable<Response<ResponseBody>> stopPtzTour(@Header("Cookie")     String cookie,
                                                   @Path  ("tenantId")   String tenantId,
                                                   @Path  ("deviceId")   String deviceId,
                                                   @Body TriggerTourData tourData);

    @PUT("api/v1/Organizations/{tenantId}/Devices/{deviceId}/Gateway/camera-zoom")
    Observable<Response<ResponseBody>> doZoom(@Header("Cookie")     String cookie,
                                                    @Path  ("tenantId")   String tenantId,
                                                    @Path  ("deviceId")   String deviceId,
                                                    @Body CameraZoomData cameraZoomData);

    @PUT("api/v1/Organizations/{tenantId}/Devices/{deviceId}/Gateway/camera-tour")
    Observable<Response<ResponseBody>> startPtzTour(@Header("Cookie")     String cookie,
                                                    @Path  ("tenantId")   String tenantId,
                                                    @Path  ("deviceId")   String deviceId,
                                                    @Body TriggerTourData tourData);

    @GET("api/v1/Organizations/{tenantId}/Devices/{deviceId}/Gateway/camera-ptz-tour")
    Observable<PtzTours> fetchPtzTours(@Header("Cookie")     String cookie,
                                        @Path  ("tenantId")   String tenantId,
                                        @Path  ("deviceId")   String deviceId,
                                        @Query ("parameters") String parameters);
    // Id%3DeyJpZCI6Ik1TNHdNREU0T0RVeE1HSTJaV1V1WTJGdE1EQT0iLCJzZXJ2ZXJJZCI6Im5OUHF6OXIvUUVxMFdRU3dlVCtoL3c9PSJ9

    @GET("api/v1/Organizations/{tenantId}/Devices/{deviceId}/Gateway/camera-ptz-preset")
    Observable<PtzPresets> fetchPtzPresets(@Header("Cookie")     String cookie,
                                           @Path  ("tenantId")   String tenantId,
                                           @Path  ("deviceId")   String deviceId,
                                           @Query ("parameters") String parameters);
    // Id%3DeyJpZCI6Ik1TNHdNREU0T0RVeE1HSTJaV1V1WTJGdE1EQT0iLCJzZXJ2ZXJJZCI6Im5OUHF6OXIvUUVxMFdRU3dlVCtoL3c9PSJ9

    @PUT("api/v1/Organizations/{tenantId}/Devices/{deviceId}/Gateway/camera-preset")
    Observable<Response<ResponseBody>> triggerPtzPreset(@Header("Cookie")     String cookie,
                                                        @Path  ("tenantId")   String tenantId,
                                                        @Path  ("deviceId")   String deviceId,
                                                        @Body TriggerPresetData presetData);

    @PUT("api/v1/Organizations/{tenantId}/Devices/{deviceId}/Gateway/camera-preset-home")
    Observable<Response<ResponseBody>> triggerPtzPresetHome(@Header("Cookie")     String cookie,
                                                            @Path  ("tenantId")   String tenantId,
                                                            @Path  ("deviceId")   String deviceId,
                                                            @Body TriggerPresetData presetData);


    @PUT("api/v1/Organizations/{tenantId}/Devices/{deviceId}/Gateway/camera-pan-tilt-zoom")
    Observable<Response<ResponseBody>> updateCameraPanTiltZoom(@Header("Cookie")     String cookie,
                                                               @Path  ("tenantId")   String tenantId,
                                                               @Path  ("deviceId")   String deviceId,
                                                               @Body CameraPtzData data);


    @GET("odata/v1/Alarms({tenantId})")
    Observable<Alarms> getAlarms(@Header("Cookie") String cookie,
                                 @Path("tenantId") String tenantId,
                                 @Query("$filter") String filter,
                                 @Query("$top") int top,
                                 @Query("$count") String count);

    @GET
    Observable<Alarms> fetchMoreAlarms(@Header("Cookie") String cookie, @Url String url);

    @GET("api/v1/Organizations/{tenantId}/Alarms/{alarmId}")
    Observable<Alarm> fetchAlarm(@Header("Cookie") String cookie,
                                 @Path("tenantId") String tenantId,
                                 @Path("alarmId") String alarmId);

    @GET("api/v1/Organizations/{tenantId}/CameraGroups/{id}/Cameras")
    Observable<List<Camera>> fetchCamerasWithLocationId(@Header("Cookie") String cookie,
                                                        @Path("tenantId") String tenantId,
                                                        @Path("id") String cameraGroupId);

    //https://ci.develophelios.com/api/v1/Organizations/bb1410ea-8c07-e911-9461-00155d9c1016/Cameras?$filter=((DeviceId%20eq%2055d593ff-e823-e911-9461-00155d96a8f1))
    @GET("api/v1/Organizations/{tenantId}/Cameras")
    Observable<List<Camera>> fetchCameras(@Header("Cookie") String cookie,
                                          @Path("tenantId") String tenantId,
                                          @Query("$filter") String filter);

    //https://ci.develophelios.com/api/v1/Organizations/bb1410ea-8c07-e911-9461-00155d9c1016/Devices?$filter=((SiteId%20eq%207551df2d-8d07-e911-9461-00155d9c1016))
    @GET("api/v1/Organizations/{tenantId}/Devices")
    Observable<List<Device>> fetchDevices(@Header("Cookie") String cookie,
                                          @Path("tenantId") String tenantId,
                                          @Query("$filter") String filter);

    @GET("api/v1/Organizations/{tenantId}/Devices/{deviceId}")
    Observable<Device> fetchDeviceForId(@Header("Cookie") String cookie,
                                        @Path("tenantId") String tenantId,
                                        @Path("deviceId") String deviceId);

    @GET("api/v1/Organizations/{tenantId}/Devices/{deviceId}/Gateway/media")
    Observable<StreamDetails> getMedia(@Header("Cookie") String cookie,
                                       @Path("tenantId") String tenantId,
                                       @Path("deviceId") String deviceId,
                                       @Query("parameters") String parameters);
    // cameraId%3DeyJpZCI6Ik1TNHdNREU0T0RVeE1HSTJaV1V1WTJGdE1EQT0iLCJzZXJ2ZXJJZCI6Im5OUHF6OXIvUUVxMFdRU3dlVCtoL3c9PSJ9%26format%3Dp2p

    @POST("api/v1/Organizations/{tenantId}/Devices/{deviceId}/Gateway/p2p-media")
    Observable<StreamDetails> getP2PMedia(@Header("Cookie") String cookie,
                                          @Path("tenantId") String tenantId,
                                          @Path("deviceId") String deviceId,
                                          @Body P2pMediaParams mediaParams);

    @GET("api/v1/Organizations/{tenantId}/Locations/{id}/Devices")
    Observable<List<Device>> getDevicesWithLocationId(@Header("Cookie") String cookie,
                                                      @Path("tenantId") String tenantId,
                                                      @Path("id") String locationId);

    @GET("/api/v1/Organizations/{tenantId}/Locations")
    Observable<List<Site>> fetchLocations(@Header("Cookie") String cookie, @Path("tenantId") String tenantId);

    @GET("api/v1/Organizations/{tenantId}/Locations/{locationId}")
    Observable<Site> fetchLocation(@Header("Cookie")   String cookie,
                                   @Path("tenantId")   String tenantId,
                                   @Path("locationId") String locationId);


    //https://ci.develophelios.com/api/v1/Organizations/bb1410ea-8c07-e911-9461-00155d9c1016/Devices?$filter=((SiteId%20eq%207551df2d-8d07-e911-9461-00155d9c1016))
    @GET("api/v1/Organizations/{tenantId}/Locations")
    Observable<List<Site>> fetchLocationsWithFilter(@Header("Cookie") String cookie,
                                                    @Path("tenantId") String tenantId,
                                                    @Query("$filter") String filter);

    @POST("api/v1/Organizations/{tenantId}/Devices/{deviceId}/Cameras/{cameraId}/Snapshot")
    Observable<Response<String>> refreshSnapshot(@Header("Cookie")  String cookie,
                                                 @Path  ("tenantId")String tenantId,
                                                 @Path  ("deviceId")String deviceId,
                                                 @Path  ("cameraId")String cameraId);

    @GET("api/v1/Organizations/{tenantId}/Devices/{deviceId}/Cameras/{cameraId}/Snapshot")
    Observable<SnapshotUrlResponse> getSnapshot( @Header("Cookie")  String cookie,
                                                 @Path  ("tenantId")String tenantId,
                                                 @Path  ("deviceId")String deviceId,
                                                 @Path  ("cameraId")String cameraId);

    @POST("api/v1/Accounts/ForgotPassword")
    Observable<Void> sendForgotPasswordEmail(@Query("email") String email);

    @POST("api/v1/Accounts/Login")
    Observable<Response<SigninResponse>> postLogin(@Body SigninRequest signinRequest);

    @POST("api/v1/Accounts/EulaAcceptanceRequest")
    Observable<Response<String>> sendEulaAcceptance(@Body SecurityToken securityToken);

    @POST
    Observable<Response<String>> sendEulaAcceptance(@Url String url, @Body SecurityToken securityToken);

    @GET("api/v1/Accounts/EulaDocumentUri")
    Observable<SigninResponse> getEULAUrl();

    @POST("api/v1/Accounts/Logout")
    Observable<Response<ResponseBody>> postLogout(@Header("Cookie") String cookie, @Body SignoutRequest body);

    @GET("api/v1/Accounts/Profile")
    Observable<Profile> fetchProfile(@Header("Cookie") String cookie);

    @PUT("api/v1/Accounts/Profile")
    Observable<Response<ResponseBody>> updateProfile(@Header("Cookie") String cookie, @Body Profile profile);

    @GET("api/v1/Organizations/{id}")
    Observable<Organization> fetchOrganization(@Header("Cookie") String cookie, @Path("id") String organizationId);

    @GET("api/v1/Organizations/{tenantId}/Accounts/Users")
    Observable<ArrayList<Tenant>> getAvatars(@Header("Cookie") String cookie,
                                             @Path("tenantId") String tenantId);

    @GET("api/v1/Accounts/Users")
    Observable<ArrayList<Tenant>> fetchTenants(@Header("Cookie") String cookie);

    @PUT("api/v1/Accounts/Users/{id}")
    Observable<Response<ResponseBody>> switchAvatar(@Header("Cookie") String cookie, @Path("id") String avatarId, @Query("clientId") String clientId);

    @GET("api/v1/Organizations/{tenantId}/Users/{userId}")
    Observable<Profile> fetchUserProfile(@Header("Cookie") String cookie, @Path("tenantId") String tenantId, @Path("userId")String usserId);


    @GET("api/v1/Organizations/{tenantId}/Clips/{clipId}")
    Observable<ClipUrlResponse> getClipUrl(@Header("Cookie")  String cookie,
                                           @Path  ("tenantId")String tenantId,
                                           @Path  ("clipId")  String clipId);

    @GET("api/v1/Organizations/{tenantId}/UsersPublicInfo/{id}")
    Observable<User> getUserPublicInfo(@Header("Cookie") String cookie, @Path("tenantId") String tenantId, @Path("id") String userId);

    @GET
    @Streaming
    Observable<Response<ResponseBody>> fetchVideoClip(@Url String url);

    @GET("api/v1/Utilities/Regions")
    Observable<RegionSettings> getRegions();

    @GET("api/v1/Accounts/Customers")
    Observable<List<Subscriber>> fetchSubscribersForDealer(@Header("Cookie") String cookie);

    @GET("api/v1/Organizations/{subscriberOrgId}/Locations")
    Observable<List<Site>> fetchLocationsForSubscriber(@Header("Cookie") String cookie, @Path("subscriberOrgId") String subscriberOrgId);

    @GET("api/v2/Utilities/Regions")
    Observable<RegionSettingsV2> getRegionsV2(@Header("Cookie") String cookie);

    @GET("api/v1/Utilities/Links")
    Observable<List<KeyValue>> getLinks(@Header("Cookie") String cookie);
    */
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
