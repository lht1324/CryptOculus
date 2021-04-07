package org.techtown.cryptoculus.pojo


import com.google.gson.annotations.SerializedName

data class TickerBithumb(
    @SerializedName("acc_trade_value")
    val accTradeValue: String,
    @SerializedName("acc_trade_value_24H")
    val accTradeValue24H: String,
    @SerializedName("closing_price")
    val closingPrice: String,
    @SerializedName("fluctate_24H")
    val fluctate24H: String,
    @SerializedName("fluctate_rate_24H")
    val fluctateRate24H: String,
    @SerializedName("max_price")
    val maxPrice: String,
    @SerializedName("min_price")
    val minPrice: String,
    @SerializedName("opening_price")
    val openingPrice: String,
    @SerializedName("prev_closing_price")
    val prevClosingPrice: String,
    @SerializedName("units_traded")
    val unitsTraded: String,
    @SerializedName("units_traded_24H")
    val unitsTraded24H: String
) : Ticker()