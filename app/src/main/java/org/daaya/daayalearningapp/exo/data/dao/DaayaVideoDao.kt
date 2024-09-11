package org.daaya.daayalearningapp.exo.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import org.daaya.daayalearningapp.exo.network.objects.DaayaVideo

@Dao
interface DaayaVideoDao {
    @get:Query("SELECT * FROM daayavideo")
    val videos: List<DaayaVideo>

    @Insert
    suspend fun insertAll(videos: List<DaayaVideo>)

    @Query("DELETE FROM daayavideo")
    suspend fun deleteAll()

    @Query("SELECT * FROM daayavideo")
    fun observeAll(): Flow<List<DaayaVideo>>

    @Query("SELECT * FROM daayavideo WHERE filename = :videofilename")
    fun observeById(videofilename: String): Flow<DaayaVideo>

    @Query("SELECT * FROM daayavideo WHERE filename = :videofilename")
    suspend fun getById(videofilename: String): DaayaVideo?

    /**
     * Insert or update a video in the database. If a video already exists, replace it.
     *
     * @param video the video to be inserted or updated.
     */
    @Upsert
    suspend fun upsert(video: DaayaVideo)

    /**
     * Insert or update videos in the database. If a video already exists, replace it.
     *
     * @param videos the videos to be inserted or updated.
     */
    @Upsert
    suspend fun upsertAll(videos: List<DaayaVideo>)

    /**
     * Delete a video by id.
     *
     * @return the number of tasks deleted. This should always be 1.
     */
    @Query("DELETE FROM daayavideo WHERE filename = :videofilename")
    suspend fun deleteById(videofilename: String): Int
}

