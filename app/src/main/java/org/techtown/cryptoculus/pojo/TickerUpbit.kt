package org.techtown.cryptoculus.pojo

import com.google.gson.annotations.SerializedName

data class TickerUpbit(
        @SerializedName("opening_price")
        override var open: String,
        @SerializedName("trade_price")
        override var close: String,
        @SerializedName("high_price")
        override var max: String,
        @SerializedName("low_price")
        override var min: String,
        @SerializedName("prev_closing_price")
        override var yesterdayClose: String,
        @SerializedName("acc_trade_volume_24h")
        override var tradeVolume: String,
        val market: String
) : Ticker()