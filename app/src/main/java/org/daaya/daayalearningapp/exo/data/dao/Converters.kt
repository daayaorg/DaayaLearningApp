package org.daaya.daayalearningapp.exo.data.dao

import androidx.room.TypeConverter
import org.daaya.daayalearningapp.exo.network.objects.DaayaVideoTaxonomy

class Converters {
    @TypeConverter
    fun fromString(value: String?): DaayaVideoTaxonomy? {
        return DaayaVideoTaxonomy.fromString(value)
    }

    @TypeConverter
    fun taxonomyToString(taxonomy: DaayaVideoTaxonomy?): String? {
        return taxonomy.toString()
    }
}