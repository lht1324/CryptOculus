package org.techtown.cryptoculus.pojo

import com.google.gson.annotations.SerializedName

data class TickerBithumb(
        @SerializedName("opening_price")
        override var open: String,
        @SerializedName("closing_price")
        override var close: String,
        @SerializedName("max_price")
        override var max: String,
        @SerializedName("min_price")
        override var min: String,
        @SerializedName("prev_closing_price")
        override var yesterdayClose: String,
        @SerializedName("units_traded_24H")
        override var tradeVolume: String
) : Ticker()