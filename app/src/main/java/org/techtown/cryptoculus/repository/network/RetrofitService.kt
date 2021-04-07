package org.techtown.cryptoculus.repository.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    // https://api.coinone.co.kr/ticker/?currency=all
    @GET("ticker?")
    fun getTickersCoinone(@Query("currency") currency: String?): Call<Any>

    // https://api.bithumb.com/public/ticker/ALL_KRW
    @GET("public/ticker/ALL_KRW")
    fun getTickersBithumb(): Call<Any>

    // https://api.upbit.com/v1/
    // https://api.upbit.com/v1/market/all (Markets)
    // https://api.upbit.com/v1/ticker?markets=... (Ticker)
    @GET("market/all")
    fun getMarketsUpbit(): Call<Any>
    // fun getMarketsUpbit(): Call<Any>
    @GET("ticker?")
    fun getTickersUpbit(@Query("markets") markets: String?): Call<Any>

    // https://api-cloud.huobi.co.kr/market/tickers
    @GET("market/tickers")
    fun getTickersHuobi(): Call<Any>
}