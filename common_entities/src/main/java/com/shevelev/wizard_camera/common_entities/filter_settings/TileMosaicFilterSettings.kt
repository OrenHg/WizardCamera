package com.shevelev.wizard_camera.common_entities.filter_settings

import android.os.Parcelable
import com.shevelev.wizard_camera.common_entities.enums.FilterCode
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TileMosaicFilterSettings(
    override val code: FilterCode = FilterCode.TILE_MOSAIC,

    /**
     * from 40(included) to 100(included)
     */
    val tileSize: Int,

    /**
     * from 1(included) to 5(included)
     */
    val borderSize: Int
): FilterSettings, Parcelable