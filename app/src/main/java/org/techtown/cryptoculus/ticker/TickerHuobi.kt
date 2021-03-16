package org.techtown.cryptoculus.ticker


import com.google.gson.annotations.SerializedName

data class TickerHuobi(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("ask")
    val ask: String,
    @SerializedName("askSize")
    val askSize: String,
    @SerializedName("bid")
    val bid: String,
    @SerializedName("bidSize")
    val bidSize: String,
    @SerializedName("close")
    val close: String,
    @SerializedName("count")
    val count: String,
    @SerializedName("high")
    val high: String,
    @SerializedName("low")
    val low: String,
    @SerializedName("open")
    val open: String,
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("vol")
    val vol: String
) : Ticker()