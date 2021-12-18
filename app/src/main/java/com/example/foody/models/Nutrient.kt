package com.example.foody.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Nutrient (
    @SerializedName("name")
    val name: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("unit")
    val unit: String,
    @SerializedName("percentOfDailyNeeds")
    var percentOfDailyNeeds: Double
): Parcelable{

    fun amountStr():String{
        return "${amount.toInt()}"
    }
}