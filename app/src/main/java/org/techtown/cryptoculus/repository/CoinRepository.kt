package org.techtown.cryptoculus.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.core.Observable
import org.techtown.cryptoculus.repository.model.CoinDao
import org.techtown.cryptoculus.repository.model.CoinDatabase
import org.techtown.cryptoculus.repository.model.CoinInfo
import org.techtown.cryptoculus.repository.network.Client
import org.techtown.cryptoculus.repository.network.DataParser

class CoinRepository(application: Application) {
    private val coinDao: CoinDao by lazy { CoinDatabase.getInstance(application)!!.coinDao() }
    private val coinInfos: ArrayList<CoinInfo> by lazy { coinDao.getAll() as ArrayList<CoinInfo> }
    val client = Client().retrofitService
    val response = MutableLiveData<String>()

    val coinone = "coinone"
    val bithumb = "bithumb"
    val upbit = "upbit"
    val huobi = "huobi"
    val coinoneUrl = "https://api.coinone.co.kr/"
    val bithumbUrl = "https://api.bithumb.co.kr/"
    val huobiUrl = "https://api-cloud.huobi.co.kr/"
    val upbitUrl = "https://api.upbit.com/v1/"
    var url = ""

    // dataParser.parseUpbit(
    //     client.getUpbitTickers(
    //         dataParser.parseUpbitMarkets(
    //             client.getUpbitMarkets()
    //         )
    //     )
    // )
    // Markets를 구한다 (getResponse, parse) -> Tickers를 얻어온다 (getResponse(markets), parse)

    fun getData(exchange: String): MutableLiveData<String> { // 실행 시 parsing까지 마친 ArrayList<CoinInfo>를 출력
        val parser = DataParser()
        val call: retrofit2.Call<Any> =
                when (url) {
                    coinoneUrl ->
                        client.getTickersCoinone("all")
                    bithumbUrl ->
                        client.getTickersBithumb()
                    upbitUrl -> {
                        client.getTickersUpbit(getData("upbitMarket").value)
                        /* if (value.isNotBlank()) client.getMarketsUpbit()
                        else client.getTickersUpbit(value) */
                    }
                    huobiUrl ->
                        client.getTickersHuobi()
                    else ->
                        client.getMarketsUpbit()
                }

        call.enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: retrofit2.Call<Any>, response: retrofit2.Response<Any>) {
                parser.processData(exchange, response.body().toString())
                // processData without parameters?
                // 확실히 저거면 by lazy로 coinInfos 만들 수 있긴 하지
                /* when (url) {
                    coinoneUrl ->
                        parser.parseCoinone(response.body().toString())
                    bithumbUrl ->
                        parseBithumb(response.body().toString())
                    upbitUrl -> {
                        if (value.isBlank()) parseUpbitMarkets(response.body().toString())
                        else parseUpbitTickers(response.body().toString(), value)
                    }
                    huobiUrl ->
                        parseHuobi(response.body().toString())
                    else -> {
                        println("Connection failed in onResponse.")
                        return
                    }
                } */
            }
            override fun onFailure(call: retrofit2.Call<Any>, t: Throwable) {
                println("Retrofit process is failed.")
            }
        })
        response.value = ""
        return response
    }

    fun getCoinInfo(exchange: String, coinName: String): LiveData<CoinInfo> {
        return coinDao.getCoinInfo(exchange, coinName)
    }

    fun getCoinInfos(): LiveData<ArrayList<CoinInfo>> {
        return coinDao.getAll() as LiveData<ArrayList<CoinInfo>> // 오류 가능성 존재
    }

    fun insert(coinInfo: CoinInfo): Observable<Unit> {
        return Observable.fromCallable { coinDao.insert(coinInfo) }
    }

    fun update(coinInfo: CoinInfo): Observable<Unit> {
        return Observable.fromCallable { coinDao.update(coinInfo) }
    }

    fun insertAll(coinInfos: List<CoinInfo>): Observable<Unit> {
        return Observable.fromCallable { coinDao.insertAll(coinInfos) }
    }

    fun updateAll(coinInfos: List<CoinInfo>): Observable<Unit> {
        return Observable.fromCallable { coinDao.updateAll(coinInfos) }
    }
}