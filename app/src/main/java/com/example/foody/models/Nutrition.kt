package com.example.foody.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Nutrition (
    @SerializedName("nutrients")
    val nutrients: @RawValue List<Nutrient>,
        ): Parcelable