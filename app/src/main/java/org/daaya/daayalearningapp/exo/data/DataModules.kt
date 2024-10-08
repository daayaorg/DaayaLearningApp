/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.daaya.daayalearningapp.exo.data

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.daaya.daayalearningapp.exo.data.dao.DaayaVideoDao
import org.daaya.daayalearningapp.exo.data.dao.DaayaVideosDatabase
import org.daaya.daayalearningapp.exo.data.network.NetworkDataSource
import org.daaya.daayalearningapp.exo.data.network.VideoNetworkDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindTaskRepository(repository: DefaultVideosRepository): VideosRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Singleton
    @Binds
    abstract fun bindNetworkDataSource(dataSource: VideoNetworkDataSource): NetworkDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): DaayaVideosDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            DaayaVideosDatabase::class.java,
            "Videos.db"
        ).build()
    }

    @Provides
    fun provideTaskDao(database: DaayaVideosDatabase): DaayaVideoDao = database.daayaVideoDao()
}
