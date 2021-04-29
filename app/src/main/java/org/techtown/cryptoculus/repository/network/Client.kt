package org.techtown.cryptoculus.repository.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.techtown.cryptoculus.pojo.UpbitMarket
import org.techtown.cryptoculus.repository.model.CoinInfo
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class Client {
    fun getData(exchange: String): Single<Response<Any>> {
        println("exchange in Client = $exchange")
        return when (exchange) {
            "Coinone" -> getService("https://api.coinone.co.kr/").getCoinone("all")
            "Bithumb" -> getService("https://api.bithumb.com/").getBithumb()
            "Upbit" -> getService("https://api.upbit.com/v1/").getUpbit(getMarketsUpbit()) as Single<Response<Any>>
            else -> getService("https://api-cloud.huobi.co.kr/").getHuobi() as Single<Response<Any>>
        }
    }

    private fun getMarketsUpbit(): String {
        var markets = ""
        val marketList = getService("https://api.upbit.com/v1/").getMarketsUpbit().execute().body()

        for (i in marketList!!.indices) {
            if (marketList[i].market.contains("KRW-"))
                markets += "${marketList[i].market},"
        }
        
        markets = markets.substring(0, markets.lastIndex) // 마지막에 붙은 ',' 삭제
        return markets
    }

    private fun getService(baseUrl: String): RetrofitService = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)

    private fun println(data: String) = Log.d("Client", data)
}