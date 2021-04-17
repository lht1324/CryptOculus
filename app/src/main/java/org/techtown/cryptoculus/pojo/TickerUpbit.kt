package org.techtown.cryptoculus.pojo

import com.google.gson.annotations.SerializedName

data class TickerUpbit(
        val market: String,
        @SerializedName("opening_price")
        val openingPrice: String,
        @SerializedName("trade_price")
        val tradePrice: String,
        @SerializedName("high_price")
        val highPrice: String,
        @SerializedName("low_price")
        val lowPrice: String,
        @SerializedName("acc_trade_volume_24h")
        val tradeVolume: String
) : Ticker()