package org.techtown.cryptoculus.repository.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.techtown.cryptoculus.pojo.UpbitMarket
import org.techtown.cryptoculus.repository.model.CoinInfo
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class Client {
    fun getDataCoinone() = getData("https://api.coinone.co.kr/").getCoinone("all")

    fun getDataBithumb() = getData("https://api.bithumb.com/").getBithumb()

    fun getDataUpbit() = getData("https://api.upbit.com/v1/").getUpbit(getMarketsUpbit())

    fun getDataHuobi() = getData("https://api-cloud.huobi.co.kr/").getHuobi()

    private fun getMarketsUpbit(): String {
        var markets = ""
        val marketList = getData("https://api.upbit.com/v1/").getMarketsUpbit().execute().body()

        for (i in marketList!!.indices) {
            if (marketList[i].market.contains("KRW-"))
                markets += "${marketList[i].market},"
        }
        
        markets = markets.substring(0, markets.lastIndex) // 마지막에 붙은 ',' 삭제
        return markets
    }

    private fun getData(baseUrl: String): RetrofitService = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)

    private fun println(data: String) = Log.d("Parser", data)

    /* fun getResponse(exchange: String) = when(exchange) {
        "coinone" -> getData(coinoneBaseUrl).getTickersCoinone("all")
        "bithumb" -> getData(bithumbBaseUrl).getTickersBithumb()
        "upbit" -> {
            val tempMarkets = ArrayList<UpbitMarket>()
            var markets = ""
            Observable.fromIterable(getData(upbitBaseUrl).getMarketsUpbit().execute().body())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter { upbitMarket -> upbitMarket.market.contains("KRW")}
                .subscribe {
                    println("Observable in Client is executed.")
                    println("it = $it")
                    tempMarkets.add(it)
                }

            for (i in tempMarkets.indices) {
                markets += if (i < tempMarkets.size - 1)
                    "${tempMarkets[i].market},"
                else
                    tempMarkets[i].market
            }
            println("markets = $markets")
            getData(upbitBaseUrl).getTickersUpbit(markets)
        }
        else -> getData(huobiBaseUrl).getTickersHuobi()
    } */

    /* fun getData(exchange: String) { // 실행 시 parsing까지 마친 ArrayList<CoinInfo>를 출력
        markets = ""
        val url = when(exchange) {
            coinone -> "https://api.coinone.co.kr/"
            bithumb -> "https://api.bithumb.com/"
            upbit -> "https://api.upbit.com/v1/"
            else -> "https://api-cloud.huobi.co.kr/"
        }

        /* if (exchange == "upbit") {
            getMarketsUpbit(url)
                    .getMarketsUpbit()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap { marketsTemp -> Observable.fromIterable(marketsTemp)
                                .filter {
                                    it.market.contains("KRW")
                                }.toList().toObservable()
                    }
                    .subscribe { marketsTemp ->
                        for (i in marketsTemp.indices) {
                            println("market[$i] = ${marketsTemp[i].market}")
                            markets += if (i != marketsTemp.size - 1)
                                "${marketsTemp[i]},"
                            else
                                marketsTemp[i]
                        }
                        println("markets = $markets")
                    }
        } */

        getResponse(url, exchange)
    }

    private fun getResponse(url: String, exchange: String) {
        val parser = DataParser()
        var markets = ""

        val builder = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)

        if (exchange == "upbit")
            markets = parser.parseUpbitMarkets(builder.getMarketsUpbit().execute().body().toString())
        println("markets = $markets")

        val call: retrofit2.Call<Any> =
            when (exchange) {
                coinone ->
                    builder.getTickersCoinone("all")
                bithumb ->
                    builder.getTickersBithumb()
                upbit ->
                    builder.getTickersUpbit(markets)
                else ->
                    builder.getTickersHuobi()
            }

        call.enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: retrofit2.Call<Any>, response: retrofit2.Response<Any>) {
                coinInfos.value = parser.getParsedData(
                            if (exchange != "upbit") exchange
                            else markets,
                            response.body().toString()
                    )
            }

            override fun onFailure(call: retrofit2.Call<Any>, t: Throwable) {
                println("Retrofit process is failed.")
            }
        })
    } */

    /* private fun getMarketsUpbit(url: String): RetrofitService = Retrofit.Builder()
            .baseUrl(url)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java) */

    /* private fun getMarketsUpbit() {
        val parser = DataParser()
        var markets = ""

        val builder = Retrofit.Builder()
                .baseUrl("https://api.upbit.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitService::class.java)

        val call: retrofit2.Call<Any> = builder.getMarketsUpbit()

        call.enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: retrofit2.Call<Any>, response: retrofit2.Response<Any>) {
                publishSubject.onNext(parser.parseUpbitMarkets(response.body().toString()))
            }

            override fun onFailure(call: retrofit2.Call<Any>, t: Throwable) {
                println("Retrofit process is failed.")
            }
        })
    } */
}