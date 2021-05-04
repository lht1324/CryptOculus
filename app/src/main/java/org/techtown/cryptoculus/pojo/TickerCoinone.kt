package org.techtown.cryptoculus.pojo


import com.google.gson.annotations.SerializedName

data class TickerCoinone(
    @SerializedName("currency")
    val currency: String,
    @SerializedName("first")
    val first: String,
    @SerializedName("high")
    val high: String,
    @SerializedName("last")
    val last: String,
    @SerializedName("low")
    val low: String,
    @SerializedName("yesterday_last")
    val yesterdayLast: String,
    @SerializedName("volume")
    val volume: String
) : Ticker()