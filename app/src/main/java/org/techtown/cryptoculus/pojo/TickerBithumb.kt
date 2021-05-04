package org.techtown.cryptoculus.pojo


import com.google.gson.annotations.SerializedName

data class TickerBithumb(
        @SerializedName("opening_price")
        val openingPrice: String,
        @SerializedName("closing_price")
        val closingPrice: String,
        @SerializedName("max_price")
        val maxPrice: String,
        @SerializedName("min_price")
        val minPrice: String,
        @SerializedName("prev_closing_price")
        val prevClosingPrice: String,
        @SerializedName("units_traded")
        val unitsTraded: String
) : Ticker()