package org.daaya.daayalearningapp.exo.data.dao;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import org.daaya.daayalearningapp.exo.network.objects.DaayaVideo;


@Database(entities = {DaayaVideo.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class DaayaVideosDatabase extends RoomDatabase {
    public abstract DaayaVideoDao daayaVideoDao();
}

