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
    // val client = Client().retrofitService
    var executeNetwork = true // true -> client, false -> dao
    var exchange = "coinone"

    // DB에서 가져오든 Network에서 받아오든
    // 결국 coinInfos는 바뀐다

    fun getCoinInfos(): MutableLiveData<ArrayList<CoinInfo>> {
        return if (executeNetwork)
            Client.getData(exchange)
        else
            coinDao.getAll() as MutableLiveData<ArrayList<CoinInfo>>
        // return coinDao.getAll() as MutableLiveData<ArrayList<CoinInfo>> // 오류 가능성 존재
        // 이게 아니라 coinInfos를 해야 하는 것 아닐까
    }

    /* fun getData(exchange: String): MutableLiveData<ArrayList<CoinInfo>> { // 실행 시 parsing까지 마친 ArrayList<CoinInfo>를 출력
        val parser = DataParser()
        lateinit var coinInfos: ArrayList<CoinInfo>
        val call: retrofit2.Call<Any> =
                when (exchange) {
                    coinone ->
                        client.getTickersCoinone("all")
                    bithumb ->
                        client.getTickersBithumb()
                    upbit ->
                        client.getTickersUpbit(getMarketsUpbit())
                    else ->
                        client.getTickersHuobi()
                }

        call.enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: retrofit2.Call<Any>, response: retrofit2.Response<Any>) {
                coinInfos = parser.getParsedData(exchange, response.body().toString())
                // processData without parameters?
            }
            override fun onFailure(call: retrofit2.Call<Any>, t: Throwable) {
                println("Retrofit process is failed.")
            }
        })


    }

    fun getMarketsUpbit(): String {
        val parser = DataParser()
        val call: retrofit2.Call<Any> = client.getMarketsUpbit()
        lateinit var markets: String

        call.enqueue(object : retrofit2.Callback<Any> {
            override fun onResponse(call: retrofit2.Call<Any>, response: retrofit2.Response<Any>) {
                markets = parser.parseUpbitMarkets(response.body().toString())
            }
            override fun onFailure(call: retrofit2.Call<Any>, t: Throwable) {
                println("Retrofit process is failed.")
            }
        })

        return markets
    } */

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