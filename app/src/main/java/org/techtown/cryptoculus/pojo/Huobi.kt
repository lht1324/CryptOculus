package org.techtown.cryptoculus.pojo

data class Huobi(val data: ArrayList<TickerHuobi>)

data class TickerHuobi(
        val symbol: String,
        val open: String,
        val high: String,
        val low: String,
        val close: String,
        val amount: String
): Ticker()