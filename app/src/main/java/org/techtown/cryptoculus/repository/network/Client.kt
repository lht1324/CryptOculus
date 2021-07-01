package org.techtown.cryptoculus.repository.network

import android.util.Log
import com.bumptech.glide.RequestManager
import io.reactivex.Single
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class Client {
    fun getExchangeData(exchange: String): Single<Response<Any>> {
        return when (exchange) {
            "Coinone" -> getService("https://api.coinone.co.kr/").getCoinone("all")
            "Bithumb" -> getService("https://api.bithumb.com/").getBithumb() as Single<Response<Any>>
            else -> getService("https://api.upbit.com/v1/").getUpbit(getMarketsUpbit()) as Single<Response<Any>>
        }
    }

    private fun getMarketsUpbit(): String {
        var markets = ""
        val marketList = getService("https://api.upbit.com/v1/").getMarketsUpbit().execute().body()

        for (i in marketList!!.indices) {
            if (marketList[i].market.contains("KRW-"))
                markets += "${marketList[i].market},"
        }

        return markets.substring(0, markets.lastIndex) // 마지막에 붙은 ',' 삭제
    }

    private fun getService(baseUrl: String): RetrofitService = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)

    fun getImage(requestManager: RequestManager, fileName: String) = requestManager.asBitmap()
        .load("https://storage.googleapis.com/cryptoculus-58556.appspot.com/images/$fileName")

    private fun println(data: String) = Log.d("Client", data)
}