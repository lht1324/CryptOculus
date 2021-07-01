package org.techtown.cryptoculus.repository.network

import io.reactivex.Single
import org.techtown.cryptoculus.pojo.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    // https://api.coinone.co.kr/ticker/?currency=all
    @GET("ticker?")
    fun getCoinone(@Query("currency") currency: String?): Single<Response<Any>>

    // https://api.bithumb.com/public/ticker/ALL_KRW
    @GET("public/ticker/ALL_KRW")
    fun getBithumb(): Single<Response<Any>>

    // https://api.upbit.com/v1/
    // https://api.upbit.com/v1/market/all (Markets)
    // https://api.upbit.com/v1/ticker?markets=... (Ticker)
    @GET("market/all")
    fun getMarketsUpbit(): Call<ArrayList<UpbitMarket>>

    @GET("ticker?")
    fun getUpbit(@Query("markets") markets: String?): Single<Response<ArrayList<TickerUpbit>>>
}