/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.daaya.daayalearningapp.exo.ui.video;


import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.HttpEngine;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.GlUtil;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.database.DatabaseProvider;
import androidx.media3.database.StandaloneDatabaseProvider;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.HttpEngineDataSource;
import androidx.media3.datasource.cache.Cache;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor;
import androidx.media3.datasource.cache.SimpleCache;
import androidx.media3.datasource.cronet.CronetDataSource;
import androidx.media3.datasource.cronet.CronetUtil;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.dash.DashMediaSource;
import androidx.media3.exoplayer.drm.DefaultDrmSessionManager;
import androidx.media3.exoplayer.drm.DrmSessionManager;
import androidx.media3.exoplayer.drm.FrameworkMediaDrm;
import androidx.media3.exoplayer.drm.HttpMediaDrmCallback;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.util.EventLogger;
import androidx.media3.ui.PlayerView;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.chromium.net.CronetEngine;
import org.daaya.daayalearningapp.exo.DaayaAndroidApplication;
import org.daaya.daayalearningapp.exo.R;
import org.daaya.daayalearningapp.exo.network.objects.DaayaVideo;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

/**
 * Activity that demonstrates playback of video to an {@link android.opengl.GLSurfaceView} with
 * postprocessing of the video content using GL.
 */
@AndroidEntryPoint
public final class VideoActivity extends AppCompatActivity {
    public static String ARG_VIDEO = "ARG_VIDEO";
    public static String ARG_VIDEO_URL = "ARG_VIDEO_URL";
    private String mediaUri = DaayaAndroidApplication.baseUrl + "api/v1/stream/video1";
    private static final String ACTION_VIEW = "androidx.media3.demo.gl.action.VIEW";
    private static final String EXTENSION_EXTRA = "extension";
    private static final String DRM_SCHEME_EXTRA = "drm_scheme";
    private static final String DRM_LICENSE_URL_EXTRA = "drm_license_url";

    @Nullable
    private PlayerView playerView;
    @Nullable
    @UnstableApi
    private VideoProcessingGLSurfaceView videoProcessingGLSurfaceView;

    @Nullable
    private ExoPlayer player;


    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity);
        playerView = findViewById(R.id.player_view);
        TextView videoDesc = findViewById(R.id.video_description);

        if (getIntent().hasExtra(ARG_VIDEO)) {
            DaayaVideo video = getIntent().getParcelableExtra(ARG_VIDEO);
            assert video != null;
            videoDesc.setText(video.description);
        }
        String url = getIntent().getStringExtra(ARG_VIDEO_URL);
        if (!TextUtils.isEmpty(url)) {
            mediaUri = url;
        }

        Context context = getApplicationContext();
        boolean requestSecureSurface = getIntent().hasExtra(DRM_SCHEME_EXTRA);
        if (requestSecureSurface && !GlUtil.isProtectedContentExtensionSupported(context)) {
            Toast.makeText(context, R.string.error_protected_content_extension_not_supported, Toast.LENGTH_LONG).show();
        }

        VideoProcessingGLSurfaceView videoProcessingGLSurfaceView =
                new VideoProcessingGLSurfaceView(
                        context, requestSecureSurface, new BitmapOverlayVideoProcessor(context));
        requireNonNull(playerView);
        FrameLayout contentFrame = playerView.findViewById(R.id.exo_content_frame);
        contentFrame.addView(videoProcessingGLSurfaceView);
        this.videoProcessingGLSurfaceView = videoProcessingGLSurfaceView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initializePlayer();
        if (playerView != null) {
            playerView.onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player == null) {
            initializePlayer();
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (playerView != null) {
            playerView.onPause();
        }
        releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (playerView != null) {
            playerView.onPause();
        }
        releasePlayer();
    }

    @OptIn(markerClass = UnstableApi.class)
    private void initializePlayer() {
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri uri = ACTION_VIEW.equals(action)
                ? requireNonNull(intent.getData())
                : Uri.parse(mediaUri);
        DrmSessionManager drmSessionManager;
        if (intent.hasExtra(DRM_SCHEME_EXTRA)) {
            String drmScheme = requireNonNull(intent.getStringExtra(DRM_SCHEME_EXTRA));
            String drmLicenseUrl = requireNonNull(intent.getStringExtra(DRM_LICENSE_URL_EXTRA));
            UUID drmSchemeUuid = requireNonNull(Util.getDrmUuid(drmScheme));
            DataSource.Factory licenseDataSourceFactory = new DefaultHttpDataSource.Factory();
            HttpMediaDrmCallback drmCallback =
                    new HttpMediaDrmCallback(drmLicenseUrl, licenseDataSourceFactory);
            drmSessionManager =
                    new DefaultDrmSessionManager.Builder()
                            .setUuidAndExoMediaDrmProvider(drmSchemeUuid, FrameworkMediaDrm.DEFAULT_PROVIDER)
                            .build(drmCallback);
        } else {
            drmSessionManager = DrmSessionManager.DRM_UNSUPPORTED;
        }

        //DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(this);
        DataSource.Factory dataSourceFactory = getDataSourceFactory(this);
        MediaSource mediaSource;
        @Nullable String fileExtension = intent.getStringExtra(EXTENSION_EXTRA);
        @C.ContentType
        int type =
                TextUtils.isEmpty(fileExtension)
                        ? Util.inferContentType(uri)
                        : Util.inferContentTypeForExtension(fileExtension);
        if (type == C.CONTENT_TYPE_DASH) {
            mediaSource =
                    new DashMediaSource.Factory(dataSourceFactory)
                            .setDrmSessionManagerProvider(unusedMediaItem -> drmSessionManager)
                            .createMediaSource(MediaItem.fromUri(uri));
        } else if (type == C.CONTENT_TYPE_OTHER) {
            mediaSource =
                    new ProgressiveMediaSource.Factory(dataSourceFactory)
                            .setDrmSessionManagerProvider(unusedMediaItem -> drmSessionManager)
                            .createMediaSource(MediaItem.fromUri(uri));
        } else {
            throw new IllegalStateException();
        }

        ExoPlayer player = new ExoPlayer.Builder(getApplicationContext()).build();
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.setMediaSource(mediaSource);
        player.prepare();
        player.play();
        VideoProcessingGLSurfaceView videoProcessingGLSurfaceView =
                requireNonNull(this.videoProcessingGLSurfaceView);
        videoProcessingGLSurfaceView.setPlayer(player);
        requireNonNull(playerView).setPlayer(player);
        player.addAnalyticsListener(new EventLogger());
        this.player = player;
    }

    @OptIn(markerClass = UnstableApi.class)
    private void releasePlayer() {
        requireNonNull(playerView).setPlayer(null);
        requireNonNull(videoProcessingGLSurfaceView).setPlayer(null);
        if (player != null) {
            player.release();
            player = null;
        }
    }


    private static final boolean ALLOW_CRONET_FOR_NETWORKING = false;
    private static final String DOWNLOAD_CONTENT_DIRECTORY = "downloads";

    private static @MonotonicNonNull File downloadDirectory;

    @OptIn(markerClass = androidx.media3.common.util.UnstableApi.class)
    private static @MonotonicNonNull Cache downloadCache;

    @OptIn(markerClass = androidx.media3.common.util.UnstableApi.class)
    private static @MonotonicNonNull DatabaseProvider databaseProvider;

    private static final int MAX_CACHE_SIZE = 500_000_000;
    private static final int FRAGMENT_SIZE = 120 * 1024 * 1024;


    @OptIn(markerClass = androidx.media3.common.util.UnstableApi.class)
    private static synchronized Cache getDownloadCache(Context context) {
        if (downloadCache == null) {
            File downloadContentDirectory = new File(getDownloadDirectory(context),
                    DOWNLOAD_CONTENT_DIRECTORY);
            downloadCache = new SimpleCache(downloadContentDirectory,
                    new LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE),
                    getDatabaseProvider(context));
        }
        return downloadCache;
    }


    private static synchronized File getDownloadDirectory(Context context) {
        if (downloadDirectory == null) {
            downloadDirectory = context.getExternalFilesDir(/* type= */ null);
            if (downloadDirectory == null) {
                downloadDirectory = context.getFilesDir();
            }
        }
        Timber.e("downloadDirectory = %s", downloadDirectory.getAbsolutePath());
        return downloadDirectory;
    }


    @OptIn(markerClass = androidx.media3.common.util.UnstableApi.class)
    private static synchronized DatabaseProvider getDatabaseProvider(Context context) {
        if (databaseProvider == null) {
            databaseProvider = new StandaloneDatabaseProvider(context);
        }
        return databaseProvider;
    }

    private static DataSource.@MonotonicNonNull Factory dataSourceFactory;
    private static DataSource.@MonotonicNonNull Factory httpDataSourceFactory;

    @OptIn(markerClass = androidx.media3.common.util.UnstableApi.class)
    public static synchronized DataSource.Factory getHttpDataSourceFactory(Context context) {
        if (httpDataSourceFactory != null) {
            return httpDataSourceFactory;
        }
        context = context.getApplicationContext();
        if (Build.VERSION.SDK_INT >= 34) {
            HttpEngine httpEngine = new HttpEngine.Builder(context).build();
            httpDataSourceFactory =
                    new HttpEngineDataSource.Factory(httpEngine, Executors.newSingleThreadExecutor());
            return httpDataSourceFactory;
        }
        if (ALLOW_CRONET_FOR_NETWORKING) {
            @org.checkerframework.checker.nullness.qual.Nullable
            CronetEngine cronetEngine = CronetUtil.buildCronetEngine(context);
            if (cronetEngine != null) {
                httpDataSourceFactory =
                        new CronetDataSource.Factory(cronetEngine, Executors.newSingleThreadExecutor());
                return httpDataSourceFactory;
            }
        }
        // The device doesn't support HttpEngine or we don't want to allow Cronet, or we failed to
        // instantiate a CronetEngine.
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        CookieHandler.setDefault(cookieManager);
        httpDataSourceFactory = new DefaultHttpDataSource.Factory();
        return httpDataSourceFactory;
    }


    @OptIn(markerClass = androidx.media3.common.util.UnstableApi.class)
    private static CacheDataSource.Factory buildReadOnlyCacheDataSource(
            DataSource.Factory upstreamFactory, Cache cache) {

        AtomicBoolean dontWrite = new AtomicBoolean(/* initialValue= */ false);
        CustomDataSink.Factory dataSinkFactory = new CustomDataSink.Factory(downloadCache, FRAGMENT_SIZE, dontWrite);

        return new CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(upstreamFactory)
                .setCacheWriteDataSinkFactory(dataSinkFactory)
                .setFlags(CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
    }

    /**
     * Returns a {@link DataSource.Factory}.
     */
    public static synchronized DataSource.Factory getDataSourceFactory(Context context) {
        if (dataSourceFactory == null) {
            context = context.getApplicationContext();
            DefaultDataSource.Factory upstreamFactory =
                    new DefaultDataSource.Factory(context, getHttpDataSourceFactory(context));
            dataSourceFactory = buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache(context));
        }
        return dataSourceFactory;
    }

}