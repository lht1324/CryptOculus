package org.techtown.cryptoculus.pojo

import com.google.gson.annotations.SerializedName

data class TickerCoinone(
    @SerializedName("first")
    override var open: String,
    @SerializedName("last")
    override var close: String,
    @SerializedName("high")
    override var max: String,
    @SerializedName("low")
    override var min: String,
    @SerializedName("yesterday_last")
    override var yesterdayClose: String,
    @SerializedName("volume")
    override var tradeVolume: String,
) : Ticker()