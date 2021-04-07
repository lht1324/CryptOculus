package org.techtown.cryptoculus.pojo


import com.google.gson.annotations.SerializedName

data class TickerUpbit(
    @SerializedName("acc_trade_price")
    val accTradePrice: String,
    @SerializedName("acc_trade_price_24h")
    val accTradePrice24h: String,
    @SerializedName("acc_trade_volume")
    val accTradeVolume: String,
    @SerializedName("acc_trade_volume_24h")
    val accTradeVolume24h: String,
    @SerializedName("change")
    val change: String,
    @SerializedName("change_price")
    val changePrice: String,
    @SerializedName("change_rate")
    val changeRate: String,
    @SerializedName("high_price")
    val highPrice: String,
    @SerializedName("highest_52_week_date")
    val highest52WeekDate: String,
    @SerializedName("highest_52_week_price")
    val highest52WeekPrice: String,
    @SerializedName("low_price")
    val lowPrice: String,
    @SerializedName("lowest_52_week_date")
    val lowest52WeekDate: String,
    @SerializedName("lowest_52_week_price")
    val lowest52WeekPrice: String,
    @SerializedName("market")
    val market: String,
    @SerializedName("opening_price")
    val openingPrice: String,
    @SerializedName("prev_closing_price")
    val prevClosingPrice: String,
    @SerializedName("signed_change_price")
    val signedChangePrice: String,
    @SerializedName("signed_change_rate")
    val signedChangeRate: String,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("trade_date")
    val tradeDate: String,
    @SerializedName("trade_date_kst")
    val tradeDateKst: String,
    @SerializedName("trade_price")
    val tradePrice: String,
    @SerializedName("trade_time")
    val tradeTime: String,
    @SerializedName("trade_time_kst")
    val tradeTimeKst: String,
    @SerializedName("trade_timestamp")
    val tradeTimestamp: String,
    @SerializedName("trade_volume")
    val tradeVolume: String
) : Ticker()